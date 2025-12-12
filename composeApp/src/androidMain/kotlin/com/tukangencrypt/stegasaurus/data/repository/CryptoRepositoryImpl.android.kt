package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.modes.ChaCha20Poly1305
import org.bouncycastle.crypto.params.HKDFParameters
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import java.security.SecureRandom

actual class CryptoRepositoryImpl actual constructor(private val keyRepository: KeyRepository) : CryptoRepository {
    private val secureRandom = SecureRandom()

    actual override suspend fun generateKeyPair(): KeyPair = withContext(Dispatchers.Default) {
        try {
            val privKeyParams = X25519PrivateKeyParameters(secureRandom)
            val pubKeyParams = privKeyParams.generatePublicKey()

            val publicKey = pubKeyParams.encoded.toHex()
            val privateKey = privKeyParams.encoded.toHex()

            coroutineScope {
                keyRepository.saveKeyPair(
                    KeyPair(
                        publicKey = publicKey,
                        privateKey = privateKey
                    )
                )
            }

            return@withContext KeyPair(
                publicKey = publicKey,
                privateKey = privateKey
            )
        } catch (e: Exception) {
            throw IllegalStateException("Failed to generate key pair: ${e.message}", e)
        }
    }

    actual override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray = withContext(Dispatchers.Default) {
        try {
            val senderPriv = parsePrivateKey(senderPrivateKey)
            val senderPub = senderPriv.generatePublicKey()
            val recipientPub = parsePublicKey(recipientPublicKey)

            // 1. Shared Secret
            val sharedSecret = generateSharedSecret(senderPriv, recipientPub)

            // 2. Derive Key + nonce
            val kdfInfo = senderPub.encoded + recipientPub.encoded

            val (encryptionKey, nonce) = deriveKeyAndNonce(sharedSecret, kdfInfo)

            // 3. Encrypt plaintext
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

            val finalCiphertext = senderPub.encoded + nonce + ciphertext

            return@withContext finalCiphertext
        } catch (e: Exception) {
            throw IllegalStateException("Encryption failed: ${e.message}", e)
        }
    }

    actual override suspend fun decrypt(
        encryptedData: ByteArray,
        recipientPrivateKey: String
    ): String = withContext(Dispatchers.Default) {
        try {
            if (encryptedData.size < 32 + 12 + 16) {
                throw IllegalArgumentException("Encrypted data too short")
            }

            // 1. Extract senderPub, nonce, ciphertext
            val senderPubBytes = encryptedData.sliceArray(0..31)
            val nonce = encryptedData.sliceArray(32..43)
            val ciphertext = encryptedData.sliceArray(44..encryptedData.lastIndex)
            val senderPub = parsePublicKey(senderPubBytes.toHex())

            // 2. Shared secret
            val recipientPriv = parsePrivateKey(recipientPrivateKey)
            val recipientPub = recipientPriv.generatePublicKey()
            val sharedSecret = generateSharedSecret(recipientPriv, senderPub)

            // 3. Derive key
            val kdfInfo = senderPub.encoded + recipientPub.encoded
            val (key, _) = deriveKeyAndNonce(sharedSecret, kdfInfo)
            val cipher = ChaCha20Poly1305()
            cipher.init(
                false,
                ParametersWithIV(
                    KeyParameter(key),
                    nonce
                )
            )

            val plainBytes = ByteArray(cipher.getOutputSize(ciphertext.size))
            val len = cipher.processBytes(ciphertext, 0, ciphertext.size, plainBytes, 0)
            cipher.doFinal(plainBytes, len)

            return@withContext plainBytes.decodeToString()
        } catch (e: Exception) {
            throw IllegalStateException("Decryption failed: ${e.message}", e)
        }
    }

    private fun parsePrivateKey(hex: String) =
        X25519PrivateKeyParameters(hex.fromHex())

    private fun parsePublicKey(hex: String) =
        X25519PublicKeyParameters(hex.fromHex())

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

    private fun deriveKeyAndNonce(sharedSecret: ByteArray, info: ByteArray): Pair<ByteArray, ByteArray> {
        val hkdf = HKDFBytesGenerator(SHA256Digest())
        hkdf.init(
            HKDFParameters(
                sharedSecret,
                null,
                info
            )
        )

        val derivedKey = ByteArray(32 + 12)
        hkdf.generateBytes(derivedKey, 0, derivedKey.size)
        val encryptionKey = derivedKey.copyOfRange(0, 32)
        val nonce = derivedKey.copyOfRange(32, 44)
        return encryptionKey to nonce
    }
}

private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

private fun String.fromHex(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()