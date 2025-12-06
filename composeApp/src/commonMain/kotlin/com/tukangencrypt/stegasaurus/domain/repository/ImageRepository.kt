package com.tukangencrypt.stegasaurus.domain.repository

import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap
import com.tukangencrypt.stegasaurus.domain.model.StegaImage

interface ImageRepository {
    /**
     * Load image dari ByteArray
     * @param bytes image bytes (PNG, JPG, etc)
     * @return StegaImage dengan bitmap yang siap untuk manipulasi
     * @throws IllegalArgumentException jika bytes kosong atau invalid
     */
    suspend fun loadImage(bytes: ByteArray): StegaImage

    /**
     * Save bitmap kembali ke ByteArray format PNG
     * @param bitmap bitmap yang sudah dimodifikasi
     * @return PNG bytes yang bisa disimpan/dikirim
     * @throws IllegalArgumentException jika bitmap invalid
     * @throws IllegalStateException jika encoding gagal
     */
    suspend fun saveImage(bitmap: PlatformBitmap): ByteArray
}
