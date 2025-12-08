package com.tukangencrypt.stegasaurus.presentation.screen.home_backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.use_case.EncryptAndEmbedUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.ExtractAndDecryptUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.GenerateKeyPairUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ===== States =====
sealed class KeyState {
    object Idle : KeyState()
    data class Generated(val keyPair: KeyPair) : KeyState()
}

sealed class EmbedState {
    object Idle : EmbedState()
    object Loading : EmbedState()
    data class Success(val bytes: ByteArray) : EmbedState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return bytes.contentEquals(other.bytes)
        }
        override fun hashCode(): Int = bytes.contentHashCode()
    }
    data class Error(val message: String) : EmbedState()
}

sealed class ExtractState {
    object Idle : ExtractState()
    object Loading : ExtractState()
    data class Success(val message: String) : ExtractState()
    data class Error(val message: String) : ExtractState()
}

// ===== ViewModel =====
class HomeViewModelBackup(
    private val generateKeyPairUseCase: GenerateKeyPairUseCase,
    private val encryptAndEmbedUseCase: EncryptAndEmbedUseCase,
    private val extractAndDecryptUseCase: ExtractAndDecryptUseCase,
) : ViewModel() {

    // Key Management States
    private val _keyState = MutableStateFlow<KeyState>(KeyState.Idle)
    val keyState: StateFlow<KeyState> = _keyState.asStateFlow()

    private val _myKeyPair = MutableStateFlow<KeyPair?>(null)
    val myKeyPair: StateFlow<KeyPair?> = _myKeyPair.asStateFlow()

    private val _recipientPublicKey = MutableStateFlow("")
    val recipientPublicKey: StateFlow<String> = _recipientPublicKey.asStateFlow()

    // Encrypt & Embed States
    private val _embedState = MutableStateFlow<EmbedState>(EmbedState.Idle)
    val embedState: StateFlow<EmbedState> = _embedState.asStateFlow()

    // Extract & Decrypt States
    private val _extractState = MutableStateFlow<ExtractState>(ExtractState.Idle)
    val extractState: StateFlow<ExtractState> = _extractState.asStateFlow()

    // Loading indicator
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Generate key pair untuk user
     */
    fun generateMyKeyPair() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val keyPair = generateKeyPairUseCase()
                _myKeyPair.value = keyPair
                _keyState.value = KeyState.Generated(keyPair)
            } catch (e: Exception) {
                _keyState.value = KeyState.Idle
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set recipient public key
     */
    fun setRecipientPublicKey(publicKey: String) {
        _recipientPublicKey.value = publicKey
    }

    /**
     * Encrypt message dan embed ke image
     */
    fun encryptAndEmbed(
        imageBytes: ByteArray,
        message: String
    ) {
        // Validasi
        if (imageBytes.isEmpty()) {
            _embedState.value = EmbedState.Error("Image cannot be empty")
            return
        }

        if (message.isBlank()) {
            _embedState.value = EmbedState.Error("Message cannot be empty")
            return
        }

        val myKeyPair = _myKeyPair.value
        if (myKeyPair == null) {
            _embedState.value = EmbedState.Error("Generate your key pair first")
            return
        }

        val recipientKey = _recipientPublicKey.value
        if (recipientKey.isBlank()) {
            _embedState.value = EmbedState.Error("Enter recipient public key")
            return
        }

        viewModelScope.launch {
            try {
                _embedState.value = EmbedState.Loading
                _isLoading.value = true

                val result = encryptAndEmbedUseCase(
                    imageBytes = imageBytes,
                    plainMessage = message,
                    recipientPublicKey = recipientKey
                )

                _embedState.value = EmbedState.Success(result)
            } catch (e: IllegalArgumentException) {
                _embedState.value = EmbedState.Error(e.message ?: "Invalid input")
            } catch (e: Exception) {
                _embedState.value = EmbedState.Error("Failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Extract dan decrypt message dari image
     */
    fun extractAndDecrypt(
        imageBytes: ByteArray,
        messageSizeBytes: Int,
        senderPublicKey: String
    ) {
        // Validasi
        if (imageBytes.isEmpty()) {
            _extractState.value = ExtractState.Error("Image cannot be empty")
            return
        }

        if (messageSizeBytes <= 0) {
            _extractState.value = ExtractState.Error("Message size must be positive")
            return
        }

        val myKeyPair = _myKeyPair.value
        if (myKeyPair == null) {
            _extractState.value = ExtractState.Error("Generate your key pair first")
            return
        }

        if (senderPublicKey.isBlank()) {
            _extractState.value = ExtractState.Error("Enter sender public key")
            return
        }

        viewModelScope.launch {
            try {
                _extractState.value = ExtractState.Loading
                _isLoading.value = true

                val result = extractAndDecryptUseCase(
                    imageBytes = imageBytes,
                    messageSizeBytes = messageSizeBytes,
                    senderPublicKey = senderPublicKey
                )

                _extractState.value = ExtractState.Success(result)
            } catch (e: IllegalArgumentException) {
                _extractState.value = ExtractState.Error(e.message ?: "Invalid input")
            } catch (e: Exception) {
                _extractState.value = ExtractState.Error("Failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearEmbedResult() {
        _embedState.value = EmbedState.Idle
    }

    fun clearExtractResult() {
        _extractState.value = ExtractState.Idle
    }
}