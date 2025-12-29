package com.tukangencrypt.stegasaurus.data.image

import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual suspend fun loadActual(bytes: ByteArray): PlatformBitmap {
    val img = ImageIO.read(ByteArrayInputStream(bytes))
        ?: error("Cannot decode image")

    val rgbImg = BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB)
    val g = rgbImg.createGraphics()
    g.drawImage(img, 0, 0, null)
    g.dispose()

    return JvmBitmap(rgbImg)
}

private class JvmBitmap(
    private val image: BufferedImage
): PlatformBitmap {
    override val width: Int get() = image.width
    override val height: Int get() = image.height
    override fun getPixel(x: Int, y: Int): Int = image.getRGB(x, y)
    override fun setPixel(x: Int, y: Int, rgb: Int) {
        image.setRGB(x, y, rgb)
    }

    override fun encodePng(): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        return baos.toByteArray()
    }
}