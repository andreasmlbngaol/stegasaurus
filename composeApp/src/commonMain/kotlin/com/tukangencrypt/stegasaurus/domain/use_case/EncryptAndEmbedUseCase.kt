package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository

/**
 * Encrypt message, kemudian embed ke image
 * Flow: Message → Encrypt → ByteArray → Embed ke Image → Stega Image
 */
class EncryptAndEmbedUseCase(
    private val cryptoRepository: CryptoRepository,
    private val embedUseCase: EmbedUseCase
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray {
        try {
            // Step 1: Encrypt message
            val encryptedMessage = cryptoRepository.encrypt(
                plainMessage = plainMessage,
                recipientPublicKey = recipientPublicKey,
                senderPrivateKey = senderPrivateKey
            )

            // Step 2: Embed encrypted message ke image
            val result = embedUseCase(
                imageBytes = imageBytes,
                message = encryptedMessage
            )

            return result
        } catch (e: Exception) {
            throw IllegalStateException("Encrypt and embed failed: ${e.message}", e)
        }
    }
}
