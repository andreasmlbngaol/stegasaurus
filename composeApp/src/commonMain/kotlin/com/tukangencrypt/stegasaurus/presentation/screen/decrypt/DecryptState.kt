package com.tukangencrypt.stegasaurus.presentation.screen.decrypt

@Suppress("ArrayInDataClass")
data class DecryptState(
    val myPrivateKey: String = "",
    val messageSize: String = "",
    val selectedImageName: String? = null,
    val selectedImageBytes: ByteArray? = null,
    val decryptedMessage: String? = null,
    val errorMessage: String? = null,
    val isDecrypted: Boolean = false,
    val isLoading: Boolean = false
) {
    val decryptButtonEnabled: Boolean
        get() = myPrivateKey.isNotBlank()
                && messageSize.isNotBlank()
                && selectedImageName != null
}
