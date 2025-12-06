package com.tukangencrypt.stegasaurus.domain.model

interface PlatformBitmap {
    val width: Int
    val height: Int

    fun getPixel(x: Int, y: Int): Int
    fun setPixel(x: Int, y: Int, argb: Int)
    fun encodePng(): ByteArray
}
