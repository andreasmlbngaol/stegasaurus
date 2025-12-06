package com.tukangencrypt.stegasaurus.domain.model

data class StegaImage(
    val bitmap: PlatformBitmap,
    val originalSize: Long = 0 // untuk tracking ukuran original
) {
    val width: Int get() = bitmap.width
    val height: Int get() = bitmap.height

    /**
     * Hitung maksimal message size yang bisa disimpan
     * 3 bits per pixel (R, G, B channels)
     * 1 byte = 8 bits, jadi (width * height * 3) / 8
     */
    fun getMaxMessageSize(): Int {
        return (width * height * 3) / 8
    }
}
