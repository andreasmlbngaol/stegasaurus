package com.tukangencrypt.stegasaurus.presentation.screen.encrypt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tukangencrypt.stegasaurus.domain.use_case.EncryptAndEmbedUseCase
import com.tukangencrypt.stegasaurus.domain.use_case.KeyPairUseCase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EncryptViewModel(
    private val keyPairUseCase: KeyPairUseCase,
    private val encryptAndEmbedUseCase: EncryptAndEmbedUseCase
): ViewModel() {
    private val _state = MutableStateFlow(EncryptState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                myPrivateKey = keyPairUseCase.getPrivateKey()!!,
            )
        }
    }

    fun onRecipientPublicKeyChanged(publicKey: String) {
        _state.value = _state.value.copy(
            recipientPublicKey = publicKey.trim()
        )
    }

    fun onMessageChanged(message: String) {
        _state.value = _state.value.copy(
            message = message
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

    fun encryptAndEmbed() {
        _state.value = _state.value.copy(
            isLoading = true
        )

        val imageBytes = _state.value.selectedImageBytes
            ?: return
        val message = _state.value.message
        val myPrivateKey = _state.value.myPrivateKey
        val recipientPublicKey = _state.value.recipientPublicKey

        if(imageBytes.isEmpty()) {
            _state.value = _state.value.copy(
                errorMessage = "Image cannot be empty",
            )
            return
        }

        if(message.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Message cannot be empty",
            )
            return
        }

        if (myPrivateKey.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Generate your key pair first"
            )
            return
        }

        if (recipientPublicKey.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "Recipient public key cannot be empty",
            )
            return
        }

        viewModelScope.launch {
            try {
                val result = encryptAndEmbedUseCase(
                    imageBytes = imageBytes,
                    plainMessage = message,
                    recipientPublicKey = recipientPublicKey
                )

                _state.value = _state.value.copy(
                    embeddedImageBytes = result,
                    isEncrypted = true
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