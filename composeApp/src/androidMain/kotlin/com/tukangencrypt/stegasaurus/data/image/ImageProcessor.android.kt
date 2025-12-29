package com.tukangencrypt.stegasaurus.data.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.io.ByteArrayOutputStream

actual suspend fun loadActual(bytes: ByteArray): PlatformBitmap {
    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: error("Cannot decode image")
    return AndroidBitmap(bmp.copy(Bitmap.Config.ARGB_8888, true))
}

private class AndroidBitmap(
    private val bitmap: Bitmap
): PlatformBitmap {
    override val width: Int get() = bitmap.width
    override val height: Int get() = bitmap.height

    override fun getPixel(x: Int, y: Int): Int = bitmap[x, y]
    override fun setPixel(x: Int, y: Int, rgb: Int) {
        bitmap[x, y] = rgb
    }

    override fun encodePng(): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out.toByteArray()
    }
}