package com.tukangencrypt.stegasaurus.presentation.screen.encrypt

@Suppress("ArrayInDataClass")
data class EncryptState(
    val recipientPublicKey: String = "",
    val myPrivateKey: String = "",
    val message: String = "",
    val selectedImageName: String? = null,
    val selectedImageBytes: ByteArray? = null,
    val embeddedImageBytes: ByteArray? = null,
    val errorMessage: String? = null,
    val isEncrypted: Boolean = false,
    val isLoading: Boolean = false
) {
    val encryptButtonEnabled: Boolean
        get() = recipientPublicKey.isNotBlank()
                && myPrivateKey.isNotBlank()
                && message.isNotBlank()
                && selectedImageName != null
}