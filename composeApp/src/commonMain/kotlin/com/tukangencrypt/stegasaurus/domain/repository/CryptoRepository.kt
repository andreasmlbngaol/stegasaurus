package com.tukangencrypt.stegasaurus.domain.repository

import com.tukangencrypt.stegasaurus.domain.model.KeyPair

interface CryptoRepository {
    /**
     * Generate X25519 key pair
     * @return KeyPair dengan public dan private key dalam format hex
     */
    suspend fun generateKeyPair(): KeyPair

    /**
     * Encrypt message menggunakan X25519 + HKDF + ChaCha20-Poly1305
     * @param plainMessage message yang ingin diencrypt
     * @param recipientPublicKey public key recipient (hex encoded)
     * @param senderPrivateKey private key sender (hex encoded)
     * @return encrypted data dengan nonce dan auth tag
     */
    suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray

    /**
     * Decrypt message
     * @param encryptedData encrypted bytes
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
