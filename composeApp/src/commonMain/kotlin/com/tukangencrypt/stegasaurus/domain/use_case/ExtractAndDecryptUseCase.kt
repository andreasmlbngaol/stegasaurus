package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository

/**
 * Extract encrypted message dari image, kemudian decrypt
 * Flow: Stega Image → Extract → ByteArray → Decrypt → Message
 */
class ExtractAndDecryptUseCase(
    private val cryptoRepository: CryptoRepository,
    private val extractUseCase: ExtractUseCase
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        messageSizeBytes: Int,
        senderPublicKey: String,
        recipientPrivateKey: String
    ): String {
        try {
            // Step 1: Extract encrypted message dari image
            val encryptedMessage = extractUseCase(
                imageBytes = imageBytes,
                msgSize = messageSizeBytes
            )

            // Step 2: Decrypt message
            val decryptedMessage = cryptoRepository.decrypt(
                encryptedData = encryptedMessage,
                senderPublicKey = senderPublicKey,
                recipientPrivateKey = recipientPrivateKey
            )

            return decryptedMessage
        } catch (e: Exception) {
            throw IllegalStateException("Extract and decrypt failed: ${e.message}", e)
        }
    }
}
