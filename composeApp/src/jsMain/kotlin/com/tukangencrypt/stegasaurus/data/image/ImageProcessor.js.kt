package com.tukangencrypt.stegasaurus.data.image

import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.events.Event
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual suspend fun loadActual(bytes: ByteArray): PlatformBitmap {
    return suspendCancellableCoroutine { continuation ->
        val img = document.createElement("img") as HTMLImageElement

        var onLoadCallback: ((Event) -> Unit)? = null
        var onErrorCallback: ((Event) -> Unit)? = null

        onLoadCallback = { _: Event ->
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val ctx = canvas.getContext("2d") as CanvasRenderingContext2D

            val width = img.width
            val height = img.height

            canvas.width = width
            canvas.height = height
            ctx.drawImage(img, 0.0, 0.0)

            img.removeEventListener("load", onLoadCallback!!)
            img.removeEventListener("error", onErrorCallback!!)

            continuation.resume(JsBitmap(canvas, ctx, width, height))
        }

        onErrorCallback = { _: Event ->
            img.removeEventListener("load", onLoadCallback)
            img.removeEventListener("error", onErrorCallback!!)
            continuation.resumeWithException(Exception("Cannot decode image"))
        }

        img.addEventListener("load", onLoadCallback)
        img.addEventListener("error", onErrorCallback)

        val base64 = bytesToBase64(bytes)
        img.src = "data:image/png;base64,$base64"
    }
}

private class JsBitmap(
    private val canvas: HTMLCanvasElement,
    private val ctx: CanvasRenderingContext2D,
    override val width: Int,
    override val height: Int
) : PlatformBitmap {

    private var imageData = ctx.getImageData(0.0, 0.0, width.toDouble(), height.toDouble())

    override fun getPixel(x: Int, y: Int): Int {
        val index = (y * width + x) * 4
        val pixel = imageData.data.subarray(index, index + 4)
        val r = pixel[0].toInt() and 0xFF
        val g = pixel[1].toInt() and 0xFF
        val b = pixel[2].toInt() and 0xFF
        val a = pixel[3].toInt() and 0xFF

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    override fun setPixel(x: Int, y: Int, rgb: Int) {
        val index = (y * width + x) * 4
        val data = imageData.data

        val r = ((rgb shr 16) and 0xFF).toByte()
        val g = ((rgb shr 8) and 0xFF).toByte()
        val b = (rgb and 0xFF).toByte()
        val a = ((rgb shr 24) and 0xFF).toByte()

        val pixelBytes: Array<Byte> = arrayOf(r, g, b, a)
        data.set(pixelBytes, index)

        ctx.putImageData(imageData, 0.0, 0.0)
    }

    override fun encodePng(): ByteArray {
        val dataUrl = canvas.toDataURL("image/png")
        val base64 = dataUrl.substringAfter(",")
        return base64ToBytes(base64)
    }
}

@JsModule("./base64Utils.js")
@JsNonModule
external object Base64Utils {
    fun bytesToBase64(bytes: IntArray): String
    fun base64ToBytes(base64: String): IntArray
}

private fun bytesToBase64(bytes: ByteArray): String {
    val intArray = IntArray(bytes.size) { i -> bytes[i].toInt() and 0xFF }
    return Base64Utils.bytesToBase64(intArray)
}

private fun base64ToBytes(base64: String): ByteArray {
    val intArray = Base64Utils.base64ToBytes(base64)
    return ByteArray(intArray.size) { i -> (intArray[i] and 0xFF).toByte() }
}