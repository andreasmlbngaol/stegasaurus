package com.tukangencrypt.stegasaurus.data.image

import com.tukangencrypt.stegasaurus.domain.model.PlatformBitmap
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.events.Event
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalWasmJsInterop::class)
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

            continuation.resume(WasmBitmap(canvas, ctx, width, height))
        }

        onErrorCallback = { _: Event ->
            img.removeEventListener("load", onLoadCallback)
            img.removeEventListener("error", onErrorCallback)
            continuation.resumeWithException(Exception("Cannot decode image"))
        }

        img.addEventListener("load", onLoadCallback)
        img.addEventListener("error", onErrorCallback)

        val base64 = bytesToBase64(bytes)
        img.src = "data:image/png;base64,$base64"
    }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
@OptIn(ExperimentalWasmJsInterop::class)
private class WasmBitmap(
    val canvas: HTMLCanvasElement,
    val ctx: CanvasRenderingContext2D,
    override val width: Int,
    override val height: Int
): PlatformBitmap {
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
    override fun setPixel(x: Int, y: Int, argb: Int) {
        TODO()
    //        val index = (y * width + x) * 4
//        val data = imageData.data
//
//        val r = ((argb shr 16) and 0xFF).toByte()
//        val g = ((argb shr 8) and 0xFF).toByte()
//        val b = (argb and 0xFF).toByte()
//        val a = ((argb shr 24) and 0xFF).toByte()
//
//        val pixelBytes: Array<Byte> = arrayOf(r, g, b, a)
//        data.set(pixelBytes, index)
//
//        ctx.putImageData(imageData, 0.0, 0.0)
    }

    override fun encodePng(): ByteArray {
        val dataUrl = canvas.toDataURL("image/png")
        val base64 = dataUrl.substringAfter(",")
        return base64ToBytes(base64)
    }
}

private fun bytesToBase64(bytes: ByteArray): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val result = StringBuilder()

    var i = 0
    while (i < bytes.size) {
        val b1 = bytes[i++].toInt() and 0xFF
        val b2 = if(i < bytes.size) bytes[i++].toInt() and 0xFF else 0
        val b3 = if(i < bytes.size) bytes[i++].toInt() and 0xFF else 0

        val c1 = (b1 shr 2) and 0x3F
        val c2 = ((b1 and 0x03) shl 4) or ((b2 shr 4) and 0x0F)
        val c3 = ((b2 and 0x0F) shl 2) or ((b3 shr 6) and 0x03)
        val c4 = b3 and 0x3F

        result.append(chars[c1])
        result.append(chars[c2])
        result.append(if(i - 1 < bytes.size) chars[c3] else '=')
        result.append(if (i < bytes.size) chars[c4] else '=')
    }

    return result.toString()
}

private fun base64ToBytes(base64: String): ByteArray {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    val result = mutableListOf<Byte>()

    var i = 0
    while (i < base64.length) {
        val char1 = base64.getOrNull(i) ?: break
        val char2 = base64.getOrNull(i + 1) ?: break

        val c1 = chars.indexOf(char1)
        val c2 = chars.indexOf(char2)

        if (c1 < 0 || c2 < 0) break

        result.add(((c1 shl 2) or (c2 shr 4)).toByte())

        val char3 = base64.getOrNull(i + 2)
        if (char3 != null && char3 != '=') {
            val c3 = chars.indexOf(char3)
            if (c3 >= 0) {
                result.add((((c2 and 0x0F) shl 4) or (c3 shr 2)).toByte())

                val char4 = base64.getOrNull(i + 3)
                if (char4 != null && char4 != '=') {
                    val c4 = chars.indexOf(char4)
                    if (c4 >= 0) {
                        result.add((((c3 and 0x03) shl 6) or c4).toByte())
                    }
                }
            }
        }

        i += 4
    }

    return result.toByteArray()
}