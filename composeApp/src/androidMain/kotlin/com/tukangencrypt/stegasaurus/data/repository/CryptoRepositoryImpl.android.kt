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
            val senderPriv = parsePrivateKey(senderPrivateKey)
            val recipientPub = parsePublicKey(recipientPublicKey)
            val sharedSecret = generateSharedSecret(senderPriv, recipientPub)
                .also { println("ENCRYPT - Shared Secret: ${it.toHex()}") }
            val (encryptionKey, nonce) = deriveKeyAndNonce(sharedSecret)
                .also {
                    // 🔍 DEBUG: Print key dan nonce
                    println("ENCRYPT - Key: ${it.first.toHex()}")
                    println("ENCRYPT - Nonce: ${it.second.toHex()}")
                }

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
                .also { println("ENCRYPT - Plaintext (${it.size}): ${it.toHex()}") }
            val ciphertext = ByteArray(cipher.getOutputSize(plainBytes.size))
            val len = cipher.processBytes(plainBytes, 0, plainBytes.size, ciphertext, 0)
            cipher.doFinal(ciphertext, len)

            println("ENCRYPT - Ciphertext (${ciphertext.size}): ${ciphertext.toHex()}")

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
            println("DECRYPT - Ciphertext (${encryptedData.size}): ${encryptedData.toHex()}")

            val recipientPriv = parsePrivateKey(recipientPrivateKey)
            val senderPub = parsePublicKey(senderPublicKey)
            val sharedSecret = generateSharedSecret(recipientPriv, senderPub)
                .also { println("DECRYPT - Shared Secret: ${it.toHex()}") }
            val (key, nonce) = deriveKeyAndNonce(sharedSecret)
                .also {
                    println("DECRYPT - Key: ${it.first.toHex()}")
                    println("DECRYPT - Nonce: ${it.second.toHex()}")
                }

            val cipher = ChaCha20Poly1305()
            cipher.init(false, ParametersWithIV(KeyParameter(key), nonce))

            val plainBytes = ByteArray(cipher.getOutputSize(encryptedData.size))
            val len = cipher.processBytes(encryptedData, 0, encryptedData.size, plainBytes, 0)
            cipher.doFinal(plainBytes, len)

            println("DECRYPT - Plaintext (${plainBytes.size}): ${plainBytes.toHex()}")

            return plainBytes.decodeToString()
        } catch (e: Exception) {
            throw IllegalStateException("Decryption failed: ${e.message}", e)
        }
    }

    /** Parse private key hex → X25519PrivateKeyParameters */
    private fun parsePrivateKey(hex: String) =
        X25519PrivateKeyParameters(hex.fromHex())

    /** Parse public key hex → X25519PublicKeyParameters */
    private fun parsePublicKey(hex: String) =
        X25519PublicKeyParameters(hex.fromHex())

    /** Generate shared secret using X25519 */
    private fun generateSharedSecret(
        privateKey: X25519PrivateKeyParameters,
        publicKey: X25519PublicKeyParameters
    ): ByteArray {
        val secret = ByteArray(32)
        val agreement = X25519Agreement()
        agreement.init(privateKey)
        agreement.calculateAgreement(publicKey, secret, 0)
        return secret
    }

    /** Derive encryption key + nonce using HKDF-SHA256 */
    private fun deriveKeyAndNonce(sharedSecret: ByteArray): Pair<ByteArray, ByteArray> {
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
        return encryptionKey to nonce
    }
}

private fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

private fun String.fromHex(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
