package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.data.image.ImageProcessor
import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap
import com.tukangencrypt.stegasaurus.domain.model.StegaImage
import com.tukangencrypt.stegasaurus.domain.repository.ImageRepository

class ImageRepositoryImpl : ImageRepository {

    override suspend fun loadImage(bytes: ByteArray): StegaImage {
        // Validate input
        if (bytes.isEmpty()) {
            throw IllegalArgumentException("Image bytes cannot be empty")
        }

        if (bytes.size < 8) {
            throw IllegalArgumentException("Image data too small, minimum 8 bytes required")
        }

        // Load image using platform-specific implementation
        val bitmap = try {
            ImageProcessor.load(bytes)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to decode image: ${e.message}", e)
        }

        // Validate image dimensions
        if (bitmap.width < 1 || bitmap.height < 1) {
            throw IllegalArgumentException("Invalid image dimensions: ${bitmap.width}x${bitmap.height}")
        }

        // Max pixels check - prevent memory issues
        if (bitmap.width.toLong() * bitmap.height > 100_000_000) { // 100MP
            throw IllegalArgumentException("Image too large: ${bitmap.width}x${bitmap.height}")
        }

        return StegaImage(
            bitmap = bitmap,
            originalSize = bytes.size.toLong()
        )
    }

    override suspend fun saveImage(bitmap: PlatformBitmap): ByteArray {
        // Validate bitmap
        if (bitmap.width < 1 || bitmap.height < 1) {
            throw IllegalArgumentException("Invalid bitmap dimensions: ${bitmap.width}x${bitmap.height}")
        }

        // Encode bitmap to PNG bytes
        val pngBytes = try {
            bitmap.encodePng()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to encode image to PNG: ${e.message}", e)
        }

        if (pngBytes.isEmpty()) {
            throw IllegalStateException("Failed to encode image to PNG: empty result")
        }

        return pngBytes
    }
}
