package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import dev.whyoleg.cryptography.algorithms.HKDF
import dev.whyoleg.cryptography.algorithms.SHA256

class CryptoRepositoryImpl : CryptoRepository {

    private val secureRandom = SecureRandom()

    override suspend fun generateKeyPair(): KeyPair {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("XDH")
            keyPairGenerator.initialize(NamedParameterSpec("X25519"))
            val keyPair = keyPairGenerator.generateKeyPair()

            return KeyPair(
                publicKey = keyPair.public.encoded.toHex(),
                privateKey = keyPair.private.encoded.toHex()
            )
        } catch (e: Exception) {
            throw IllegalStateException("Failed to generate key pair: ${e.message}", e)
        }
    }

    override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray {
        try {
            // Generate nonce (12 bytes untuk ChaCha20-Poly1305)
            val nonce = ByteArray(12)
            secureRandom.nextBytes(nonce)

            // Derive encryption key dari shared secret
            val encryptionKey = deriveEncryptionKey(
                senderPrivateKey = senderPrivateKey,
                recipientPublicKey = recipientPublicKey
            )

            // Encrypt message menggunakan ChaCha20-Poly1305
            val cipher = Cipher.getInstance("ChaCha20-Poly1305")
            val ivSpec = IvParameterSpec(nonce)
            val keySpec = SecretKeySpec(encryptionKey, 0, 32, "ChaCha20")

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val ciphertext = cipher.doFinal(plainMessage.encodeToByteArray())

            // Return: nonce (12) + ciphertext (dengan auth tag)
            return nonce + ciphertext
        } catch (e: Exception) {
            throw IllegalStateException("Encryption failed: ${e.message}", e)
        }
    }

    override suspend fun decrypt(
        encryptedData: ByteArray,
        senderPublicKey: String,
        recipientPrivateKey: String
    ): String {
        try {
            // Validate minimum size
            if (encryptedData.size < 28) { // 12 bytes nonce + 16 bytes auth tag minimum
                throw IllegalArgumentException("Encrypted data too short")
            }

            // Extract nonce (first 12 bytes)
            val nonce = encryptedData.copyOfRange(0, 12)
            val ciphertext = encryptedData.copyOfRange(12, encryptedData.size)

            // Derive encryption key
            val encryptionKey = deriveEncryptionKey(
                senderPrivateKey = recipientPrivateKey,
                recipientPublicKey = senderPublicKey
            )

            // Decrypt message
            val cipher = Cipher.getInstance("ChaCha20-Poly1305")
            val ivSpec = IvParameterSpec(nonce)
            val keySpec = SecretKeySpec(encryptionKey, 0, 32, "ChaCha20")

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val plainBytes = cipher.doFinal(ciphertext)

            return plainBytes.decodeToString()
        } catch (e: Exception) {
            throw IllegalStateException("Decryption failed: ${e.message}", e)
        }
    }

    /**
     * Derive encryption key dari shared secret menggunakan HKDF-SHA256
     */
    private suspend fun deriveEncryptionKey(
        senderPrivateKey: String,
        recipientPublicKey: String
    ): ByteArray {
        try {
            // Perform ECDH key agreement
            val keyFactory = java.security.KeyFactory.getInstance("XDH")
            val keyAgreement = KeyAgreement.getInstance("XDH")

            // Load private key dari hex
            val privateKeyBytes = senderPrivateKey.fromHex()
            val privateKey = keyFactory.generatePrivate(
                java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes)
            )

            // Load public key dari hex
            val publicKeyBytes = recipientPublicKey.fromHex()
            val publicKey = keyFactory.generatePublic(
                java.security.spec.X509EncodedKeySpec(publicKeyBytes)
            )

            // Generate shared secret
            keyAgreement.init(privateKey)
            keyAgreement.doPhase(publicKey, true)
            val sharedSecret = keyAgreement.generateSecret()

            // Derive key menggunakan HKDF-SHA256
            val hkdf = CryptographyProvider.Default.get(HKDF(SHA256))
            val derivedKey = hkdf.deriveKey(
                inputKeyMaterial = sharedSecret,
                info = "stegasaurus-encryption".encodeToByteArray(),
                salt = null,
                outputLength = 32 // 32 bytes untuk ChaCha20 key
            )

            return derivedKey
        } catch (e: Exception) {
            throw IllegalStateException("Key derivation failed: ${e.message}", e)
        }
    }
}
