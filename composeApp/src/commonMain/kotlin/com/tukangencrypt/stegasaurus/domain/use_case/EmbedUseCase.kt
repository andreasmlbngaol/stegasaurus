package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.repository.ImageRepository

class EmbedUseCase(private val repository: ImageRepository) {

    suspend operator fun invoke(
        imageBytes: ByteArray,
        message: ByteArray
    ): ByteArray {
        // Validate inputs
        if (imageBytes.isEmpty()) {
            throw IllegalArgumentException("Image cannot be empty")
        }
        if (message.isEmpty()) {
            throw IllegalArgumentException("Message cannot be empty")
        }

        val stega = repository.loadImage(imageBytes)
        val bitmap = stega.bitmap

        // Validate message size dengan getMaxMessageSize()
        val maxMessageSize = stega.getMaxMessageSize()
        if (message.size > maxMessageSize) {
            throw IllegalArgumentException(
                "Message too large. Max size: $maxMessageSize bytes, Got: ${message.size} bytes"
            )
        }

        var bitIndex = 0
        val totalBits = message.size * 8

        loop@ for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                if (bitIndex >= totalBits) break@loop

                val pixel = bitmap.getPixel(x, y)

                // Extract ARGB channels
                val a = (pixel shr 24) and 0xFF
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                // Embed 3 bits per pixel (one in each R, G, B LSB)
                val bits = mutableListOf<Int>()
                repeat(3) {
                    if (bitIndex < totalBits) {
                        val bit = (message[bitIndex / 8].toInt() shr (7 - (bitIndex % 8))) and 1
                        bits.add(bit)
                        bitIndex++
                    } else {
                        bits.add(0)
                    }
                }

                // Modify LSB of RGB channels
                val newR = (r and 0xFFFFFFFE.toInt()) or bits[0]
                val newG = (g and 0xFFFFFFFE.toInt()) or bits[1]
                val newB = (b and 0xFFFFFFFE.toInt()) or bits[2]

                val newPixel = (a shl 24) or (newR shl 16) or (newG shl 8) or newB
                bitmap.setPixel(x, y, newPixel)
            }
        }

        return repository.saveImage(bitmap)
    }
}
