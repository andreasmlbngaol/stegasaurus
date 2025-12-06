package com.tukangencrypt.stegasaurus.data.image

import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap

object ImageProcessor {
    suspend fun load(bytes: ByteArray): PlatformBitmap {
        return loadActual(bytes)
    }
}

expect suspend fun loadActual(bytes: ByteArray): PlatformBitmap
