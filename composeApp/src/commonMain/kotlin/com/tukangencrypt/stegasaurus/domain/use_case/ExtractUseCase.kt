package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.repository.ImageRepository

class ExtractUseCase(private val repository: ImageRepository) {

    suspend operator fun invoke(
        imageBytes: ByteArray,
        msgSize: Int
    ): ByteArray {
        // Validate inputs
        if (imageBytes.isEmpty()) {
            throw IllegalArgumentException("Image cannot be empty")
        }
        if (msgSize <= 0) {
            throw IllegalArgumentException("Message size must be positive")
        }

        val stega = repository.loadImage(imageBytes)
        val bitmap = stega.bitmap

        // Validate message size
        val maxMessageSize = stega.getMaxMessageSize()
        if (msgSize > maxMessageSize) {
            throw IllegalArgumentException(
                "Message size too large. Max: $maxMessageSize bytes, Requested: $msgSize bytes"
            )
        }

        val output = ByteArray(msgSize)
        var bitIndex = 0
        val totalBits = msgSize * 8

        loop@ for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                if (bitIndex >= totalBits) break@loop

                val pixel = bitmap.getPixel(x, y)

                // Extract RGB channels
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                // Extract 3 bits per pixel (LSB of each channel)
                val bits = listOf(
                    r and 1,
                    g and 1,
                    b and 1
                )

                // Write bits to output
                for (bit in bits) {
                    if (bitIndex >= totalBits) break@loop

                    val byteIndex = bitIndex / 8
                    val bitPosition = 7 - (bitIndex % 8)

                    // Set bit in output byte
                    output[byteIndex] = (output[byteIndex].toInt() or (bit shl bitPosition)).toByte()
                    bitIndex++
                }
            }
        }

        return output
    }
}
