package com.tukangencrypt.stegasaurus.presentation.screen.decrypt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tukangencrypt.stegasaurus.domain.use_case.ExtractAndDecryptUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.KeyPairUseCase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DecryptViewModel(
    private val keyPairUseCase: KeyPairUseCase,
    private val extractAndDecryptUseCase: ExtractAndDecryptUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DecryptState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                myPrivateKey = keyPairUseCase.getPrivateKey()!!,
            )
        }
    }

    fun onMessageSizeChanged(size: String) {
        _state.value = _state.value.copy(
            messageSize = size
        )
    }

    fun pickImage() {
        viewModelScope.launch {
            val imageFile = FileKit.openFilePicker(type = FileKitType.Image)
            if (imageFile != null) {
                _state.value = _state.value.copy(
                    selectedImageName = imageFile.name,
                    selectedImageBytes = imageFile.readBytes()
                )
            }
        }
    }

    fun extractAndDecrypt() {
        _state.value = _state.value.copy(
            isLoading = true
        )

        val imageBytes = _state.value.selectedImageBytes
            ?: return
        val messageSizeStr = _state.value.messageSize
        val myPrivateKey = _state.value.myPrivateKey

        if (imageBytes.isEmpty()) {
            _state.value = _state.value.copy(
                errorMessage = "Image cannot be empty",
                isLoading = false
            )
            return
        }

        if (messageSizeStr.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Message size cannot be empty",
                isLoading = false
            )
            return
        }

        /* Message Size + 32 bytes of sender's public key + 12 bytes of nonce + 16 bytes of tag */
        val paddingSize = 32 + 12 + 16
        val messageSize = messageSizeStr.toIntOrNull()?.plus(paddingSize)
        if (messageSize == null || messageSize <= paddingSize) {
            _state.value = _state.value.copy(
                errorMessage = "Message size must be a positive number",
                isLoading = false
            )
            return
        }

        if (myPrivateKey.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Generate your key pair first",
                isLoading = false
            )
            return
        }


        viewModelScope.launch {
            try {
                val result = extractAndDecryptUseCase(
                    imageBytes = imageBytes,
                    messageSizeBytes = messageSize,
//                    senderPublicKey = senderPublicKey
                )

                _state.value = _state.value.copy(
                    decryptedMessage = result,

                    isDecrypted = true
                )
            } catch (e: IllegalArgumentException) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Invalid input",
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Failed: ${e.message}",
                )
            } finally {
                _state.value = _state.value.copy(
                    isLoading = false
                )
            }
        }
    }
}
