package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository

/**
 * Extract encrypted message dari image, kemudian decrypt
 * Flow: Stega Image → Extract → ByteArray → Decrypt → Message
 */
class ExtractAndDecryptUseCase(
    private val cryptoRepository: CryptoRepository,
    private val keyRepository: KeyRepository,
    private val extractUseCase: ExtractUseCase
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        messageSizeBytes: Int,
        senderPublicKey: String
    ): String {
        try {
            println("Extract and decrypt started")
            // Step 1: Extract encrypted message dari image
            val encryptedMessage = extractUseCase(
                imageBytes = imageBytes,
                msgSize = messageSizeBytes
            )

            println("Extracted encrypted message size: ${encryptedMessage.size}")

            // Step 2: Decrypt message
            val decryptedMessage = cryptoRepository.decrypt(
                encryptedData = encryptedMessage,
                senderPublicKey = senderPublicKey,
                recipientPrivateKey = keyRepository.getPrivateKey() ?: throw IllegalStateException("Private key not found")
            )

            println("Decrypted message: $decryptedMessage")

            return decryptedMessage
        } catch (e: Exception) {
            throw IllegalStateException("Extract and decrypt failed: ${e.message}", e)
        }
    }
}
