package com.tukangencrypt.stegasaurus.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tukangencrypt.stegasaurus.domain.use_case.EmbedUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.ExtractUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EmbedState {
    object Idle : EmbedState()
    object Loading : EmbedState()
    data class Success(val bytes: ByteArray) : EmbedState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Success) return false
            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }
    data class Error(val message: String) : EmbedState()
}

sealed class ExtractState {
    object Idle : ExtractState()
    object Loading : ExtractState()
    data class Success(val message: String) : ExtractState()
    data class Error(val message: String) : ExtractState()
}

class HomeViewModel(
    private val embedUseCase: EmbedUseCase,
    private val extractUseCase: ExtractUseCase
) : ViewModel() {

    // Embed State
    private val _embedState = MutableStateFlow<EmbedState>(EmbedState.Idle)
    val embedState: StateFlow<EmbedState> = _embedState.asStateFlow()

    // Extract State
    private val _extractState = MutableStateFlow<ExtractState>(ExtractState.Idle)
    val extractState: StateFlow<ExtractState> = _extractState.asStateFlow()

    // Loading indicator untuk UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Embed message ke dalam image
     * @param imageBytes bytes dari image file
     * @param message text message yang ingin disembunyikan
     */
    fun embedImage(
        imageBytes: ByteArray,
        message: String
    ) {
        // Validate inputs
        if (imageBytes.isEmpty()) {
            _embedState.value = EmbedState.Error("Image cannot be empty")
            return
        }

        if (message.isBlank()) {
            _embedState.value = EmbedState.Error("Message cannot be empty")
            return
        }

        // Check message length (max 65KB)
        if (message.length > 65536) {
            _embedState.value = EmbedState.Error("Message too long (max 65KB)")
            return
        }

        viewModelScope.launch {
            try {
                _embedState.value = EmbedState.Loading
                _isLoading.value = true

                val result = embedUseCase(
                    imageBytes = imageBytes,
                    message = message.encodeToByteArray()
                )

                _embedState.value = EmbedState.Success(result)
            } catch (e: IllegalArgumentException) {
                _embedState.value = EmbedState.Error(e.message ?: "Invalid input")
            } catch (e: Exception) {
                _embedState.value = EmbedState.Error("Failed to embed message: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Extract message dari image
     * @param imageBytes bytes dari image file
     * @param msgSize size dari message yang disembunyikan (dalam bytes)
     */
    fun extractMessage(
        imageBytes: ByteArray,
        msgSize: Int
    ) {
        // Validate inputs
        if (imageBytes.isEmpty()) {
            _extractState.value = ExtractState.Error("Image cannot be empty")
            return
        }

        if (msgSize <= 0) {
            _extractState.value = ExtractState.Error("Message size must be positive")
            return
        }

        if (msgSize > 65536) {
            _extractState.value = ExtractState.Error("Message size too large (max 65KB)")
            return
        }

        viewModelScope.launch {
            try {
                _extractState.value = ExtractState.Loading
                _isLoading.value = true

                val result = extractUseCase(
                    imageBytes = imageBytes,
                    msgSize = msgSize
                ).decodeToString()

                _extractState.value = ExtractState.Success(result)
            } catch (e: IllegalArgumentException) {
                _extractState.value = ExtractState.Error(e.message ?: "Invalid input")
            } catch (_: CharacterCodingException) {
                _extractState.value = ExtractState.Error("Extracted data is not valid UTF-8 text")
            } catch (e: Exception) {
                _extractState.value = ExtractState.Error("Failed to extract message: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reset state untuk clear error/success messages
     */
    fun resetStates() {
        _embedState.value = EmbedState.Idle
        _extractState.value = ExtractState.Idle
    }

    /**
     * Clear embed result setelah ditampilkan
     */
    fun clearEmbedResult() {
        _embedState.value = EmbedState.Idle
    }

    /**
     * Clear extract result setelah ditampilkan
     */
    fun clearExtractResult() {
        _extractState.value = ExtractState.Idle
    }
}
