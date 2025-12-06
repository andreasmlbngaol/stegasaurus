package com.tukangencrypt.stegasaurus.domain.model

data class CryptoResult(
    val encryptedData: ByteArray,
    val nonce: ByteArray,
    val ciphertext: ByteArray,
    val authTag: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CryptoResult) return false
        return encryptedData.contentEquals(other.encryptedData) &&
                nonce.contentEquals(other.nonce) &&
                ciphertext.contentEquals(other.ciphertext) &&
                authTag.contentEquals(other.authTag)
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + nonce.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        result = 31 * result + authTag.contentHashCode()
        return result
    }
}
