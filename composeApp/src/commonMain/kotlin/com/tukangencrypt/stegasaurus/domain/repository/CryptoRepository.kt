package com.tukangencrypt.stegasaurus.domain.repository

import com.tukangencrypt.stegasaurus.domain.model.KeyPair

interface CryptoRepository {
    /**
     * Generate X25519 key pair
     * @return KeyPair dengan public dan private key dalam format hex
     */
    suspend fun generateKeyPair(): KeyPair

    /**
     * Encrypt message menggunakan X25519 + HKDF-SHA256 + ChaCha20-Poly1305
     * @param plainMessage message yang ingin diencrypt
     * @param recipientPublicKey public key recipient (hex encoded)
     * @param senderPrivateKey private key sender (hex encoded)
     * @return encrypted data dengan format: nonce (12 bytes) + ciphertext + authTag
     */
    suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray

    suspend fun decrypt(
        encryptedData: ByteArray,
        recipientPrivateKey: String
    ): String

    /**
     * Decrypt message
     * @param encryptedData encrypted bytes (nonce + ciphertext + authTag)
     * @param senderPublicKey public key sender (hex encoded)
     * @param recipientPrivateKey private key recipient (hex encoded)
     * @return plain message
     */
    suspend fun decrypt(
        encryptedData: ByteArray,
        senderPublicKey: String,
        recipientPrivateKey: String
    ): String
}
