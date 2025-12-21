package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult
import com.tukangencrypt.stegasaurus.domain.repository.BenchmarkRepository
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
import kotlin.system.measureNanoTime

actual class BenchmarkRepositoryImpl : BenchmarkRepository {
    private val secureRandom = SecureRandom()

    private fun createBenchmarkResult(
        operationName: String,
        iterations: Int,
        totalNanos: Long
    ): BenchmarkResult {
        val totalMs = totalNanos / 1_000_000.0
        val averageMs = totalMs / iterations
        val averageTimeNs = totalNanos.toDouble() / iterations

        return BenchmarkResult(
            operationName = operationName,
            iterations = iterations,
            totalTimeMs = totalMs,
            averageTimeMs = averageMs,
            averageTimeNs = averageTimeNs
        )
    }

    actual override fun benchmarkKeyGeneration(iterations: Int): BenchmarkResult {
        val totalNanos = measureNanoTime {
            repeat(iterations) {
                val privKeyParams = X25519PrivateKeyParameters(secureRandom)
                val pubKeyParams = privKeyParams.generatePublicKey()
            }
        }

        return createBenchmarkResult(
            operationName = "Key generation (X25519)",
            iterations = iterations,
            totalNanos = totalNanos
        )
    }

    actual override fun benchmarkSharedSecret(iterations: Int): BenchmarkResult {
        // Generate Key Pairs untuk testing
        val (privKey, _) = generateKeyPair()
        val (_, otherPubKey) = generateKeyPair()

        val totalNanos = measureNanoTime {
            repeat(iterations) {
                generateSharedSecret(privKey, otherPubKey)
            }
        }

        return createBenchmarkResult(
            operationName = "Shared Secret (X25519)",
            iterations = iterations,
            totalNanos = totalNanos
        )
    }

    actual override fun benchmarkKeyDerivation(iterations: Int): BenchmarkResult {
        val (privKey, _) = generateKeyPair()
        val (_, otherPubKey) = generateKeyPair()
        val sharedSecret = generateSharedSecret(privKey, otherPubKey)
        val info = "benchmark".encodeToByteArray()

        val totalNanos = measureNanoTime {
            repeat(iterations) {
                deriveKeyAndNonce(sharedSecret, info)
            }
        }

        return createBenchmarkResult(
            operationName = "Key Derivation (HKDF-SHA256)",
            iterations = iterations,
            totalNanos = totalNanos
        )
    }

    actual override fun benchmarkEncryption(iterations: Int): BenchmarkResult {
        val (privKey, _) = generateKeyPair()
        val (_, otherPubKey) = generateKeyPair()
        val sharedSecret = generateSharedSecret(privKey, otherPubKey)
        val (encryptionKey, nonce) = deriveKeyAndNonce(sharedSecret, "test".encodeToByteArray())
        val plainMessage = "Hello World! This is a test message."

        val totalNanos = measureNanoTime {
            repeat(iterations) {
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
            }
        }

        return createBenchmarkResult(
            operationName = "Encryption (ChaCha20-Poly1305)",
            iterations = iterations,
            totalNanos = totalNanos
        )
    }

    private fun generateKeyPair(): Pair<X25519PrivateKeyParameters, X25519PublicKeyParameters> {
        val privKey = X25519PrivateKeyParameters(secureRandom)
        val pubKey = privKey.generatePublicKey()
        return privKey to pubKey
    }

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
        hkdf.init(HKDFParameters(sharedSecret, null, info))

        val derivedKey = ByteArray(32 + 12)
        hkdf.generateBytes(derivedKey, 0, derivedKey.size)

        val encryptionKey = derivedKey.copyOfRange(0, 32)
        val nonce = derivedKey.copyOfRange(32, 44)
        return encryptionKey to nonce
    }

}