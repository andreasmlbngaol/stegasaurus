package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.modes.ChaCha20Poly1305
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.security.SecureRandom

actual class CryptoRepositoryImpl : CryptoRepository {
    private val secureRandom = SecureRandom()

    actual override suspend fun generateKeyPair(): KeyPair {
        try {
            val privKeyParams = X25519PrivateKeyParameters(secureRandom)
            val pubKeyParams = privKeyParams.generatePublicKey()

            return KeyPair(
                publicKey = pubKeyParams.encoded.toHex(),
                privateKey = privKeyParams.encoded.toHex()
            )
        } catch (e: Exception) {
            throw IllegalStateException("Failed to generate key pair: ${e.message}", e)
        }
    }

    actual override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray {
        try {
            // Parse keys
            val senderPrivKeyParams = X25519PrivateKeyParameters(senderPrivateKey.fromHex())
            val recipientPubKeyParams = X25519PublicKeyParameters(recipientPublicKey.fromHex())

            // Generate shared secret using X25519
            val sharedSecret = ByteArray(32)
            val agreement = X25519Agreement()
            agreement.init(senderPrivKeyParams)
            agreement.calculateAgreement(recipientPubKeyParams, sharedSecret, 0)

            // 🔍 DEBUG: Print shared secret
            println("ENCRYPT - Shared Secret: ${sharedSecret.toHex()}")

            // Derive encryption key and nonce using HKDF-SHA256
            val hkdf = HKDFBytesGenerator(SHA256Digest())
            hkdf.init(
                org.bouncycastle.crypto.params.HKDFParameters(
                    sharedSecret,
                    null,
                    "stegasaurus-encryption".encodeToByteArray()
                )
            )

            val derivedKey = ByteArray(32 + 12)
            hkdf.generateBytes(derivedKey, 0, derivedKey.size)

            val encryptionKey = derivedKey.copyOfRange(0, 32)
            val nonce = derivedKey.copyOfRange(32, 44)

            // 🔍 DEBUG: Print key dan nonce
            println("ENCRYPT - Key: ${encryptionKey.toHex()}")
            println("ENCRYPT - Nonce: ${nonce.toHex()}")

            // Encrypt using ChaCha20-Poly1305
            val cipher = ChaCha20Poly1305()
            cipher.init(
                true,
                ParametersWithIV(
                    KeyParameter(encryptionKey),
                    nonce
                )
            )

            val plainBytes = plainMessage.encodeToByteArray()
            val ciphertext = ByteArray(cipher.getOutputSize(plainBytes.size))
            val len = cipher.processBytes(plainBytes, 0, plainBytes.size, ciphertext, 0)
            cipher.doFinal(ciphertext, len)

            // 🔍 DEBUG: Print ciphertext
            println("ENCRYPT - Plaintext length: ${plainBytes.size}")
            println("ENCRYPT - Ciphertext length: ${ciphertext.size}")
            println("ENCRYPT - Ciphertext: ${ciphertext.toHex()}")

            return ciphertext
        } catch (e: Exception) {
            throw IllegalStateException("Encryption failed: ${e.message}", e)
        }
    }

    actual override suspend fun decrypt(
        encryptedData: ByteArray,
        senderPublicKey: String,
        recipientPrivateKey: String
    ): String {
        try {
            // Validate size
            if (encryptedData.size < 16) {
                throw IllegalArgumentException("Encrypted data too short")
            }

            // 🔍 DEBUG: Print input
            println("DECRYPT - Input length: ${encryptedData.size}")
            println("DECRYPT - Input data: ${encryptedData.toHex()}")

            // Parse keys
            val recipientPrivKeyParams = X25519PrivateKeyParameters(recipientPrivateKey.fromHex())
            val senderPubKeyParams = X25519PublicKeyParameters(senderPublicKey.fromHex())

            // Generate shared secret using X25519
            val sharedSecret = ByteArray(32)
            val agreement = X25519Agreement()
            agreement.init(recipientPrivKeyParams)
            agreement.calculateAgreement(senderPubKeyParams, sharedSecret, 0)

            // 🔍 DEBUG: Print shared secret
            println("DECRYPT - Shared Secret: ${sharedSecret.toHex()}")

            // Derive encryption key using HKDF-SHA256
            val hkdf = HKDFBytesGenerator(SHA256Digest())
            hkdf.init(
                org.bouncycastle.crypto.params.HKDFParameters(
                    sharedSecret,
                    null,
                    "stegasaurus-encryption".encodeToByteArray()
                )
            )

            val derivedKey = ByteArray(32 + 12)
            hkdf.generateBytes(derivedKey, 0, derivedKey.size)

            val encryptionKey = derivedKey.copyOfRange(0, 32)
            val nonce = derivedKey.copyOfRange(32, 44)

            // 🔍 DEBUG: Print key dan nonce
            println("DECRYPT - Key: ${encryptionKey.toHex()}")
            println("DECRYPT - Nonce: ${nonce.toHex()}")

            // Decrypt using ChaCha20-Poly1305
            val cipher = ChaCha20Poly1305()
            cipher.init(
                false,
                ParametersWithIV(
                    KeyParameter(encryptionKey),
                    nonce
                )
            )

            val plainBytes = ByteArray(cipher.getOutputSize(encryptedData.size))
            val len = cipher.processBytes(encryptedData, 0, encryptedData.size, plainBytes, 0)
            cipher.doFinal(plainBytes, len)

            return plainBytes.decodeToString()
        } catch (e: Exception) {
            throw IllegalStateException("Decryption failed: ${e.message}", e)
        }
    }
}

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

private fun String.fromHex(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
