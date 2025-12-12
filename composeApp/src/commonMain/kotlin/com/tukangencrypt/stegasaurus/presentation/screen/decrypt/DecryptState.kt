package com.tukangencrypt.stegasaurus.presentation.screen.decrypt

@Suppress("ArrayInDataClass")
data class DecryptState(
//    val senderPublicKey: String = "",
    val myPrivateKey: String = "",
    val messageSize: String = "",
    val selectedImageName: String? = null,
    val selectedImageBytes: ByteArray? = null,
    val decryptedMessage: String? = null,
    val errorMessage: String? = null,
    val isDecrypted: Boolean = false,
    val isLoading: Boolean = false
) {
//    val decryptButtonEnabled: Boolean
//        get() = senderPublicKey.isNotBlank()
//                && myPrivateKey.isNotBlank()
//                && messageSize.isNotBlank()
//                && selectedImageName != null
//
    val decryptButtonEnabled: Boolean
        get() = myPrivateKey.isNotBlank()
                && messageSize.isNotBlank()
                && selectedImageName != null
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as DecryptState
//
//        if (senderPublicKey != other.senderPublicKey) return false
//        if (myPrivateKey != other.myPrivateKey) return false
//        if (messageSize != other.messageSize) return false
//        if (selectedImageName != other.selectedImageName) return false
//        if (!selectedImageBytes.contentEquals(other.selectedImageBytes)) return false
//        if (decryptedMessage != other.decryptedMessage) return false
//        if (errorMessage != other.errorMessage) return false
//        if (isDecrypted != other.isDecrypted) return false
//        if (isLoading != other.isLoading) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = senderPublicKey.hashCode()
//        result = 31 * result + myPrivateKey.hashCode()
//        result = 31 * result + messageSize.hashCode()
//        result = 31 * result + (selectedImageName?.hashCode() ?: 0)
//        result = 31 * result + selectedImageBytes.contentHashCode()
//        result = 31 * result + (decryptedMessage?.hashCode() ?: 0)
//        result = 31 * result + (errorMessage?.hashCode() ?: 0)
//        result = 31 * result + isDecrypted.hashCode()
//        result = 31 * result + isLoading.hashCode()
//        return result
//    }
}
