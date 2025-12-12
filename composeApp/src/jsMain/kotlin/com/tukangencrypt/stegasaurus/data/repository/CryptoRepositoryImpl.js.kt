package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import kotlinx.coroutines.coroutineScope
import kotlin.js.unsafeCast

// ───── External JS functions ─────
@JsModule("./crypto.js")
@JsNonModule
external object CryptoJs {
    fun generateKeyPair(): dynamic
    fun encrypt(plainMessage: String, recipientPublicKey: String, senderPrivateKey: String): dynamic
    fun decrypt(encryptedData: dynamic, recipientPrivateKey: String): String
}

actual class CryptoRepositoryImpl actual constructor(private val keyRepository: KeyRepository) : CryptoRepository {

    actual override suspend fun generateKeyPair(): KeyPair {
        val result = CryptoJs.generateKeyPair()

        val keyPair = KeyPair(
            publicKey = result.publicKey as String,
            privateKey = result.privateKey as String
        )

        coroutineScope {
            keyRepository.saveKeyPair(keyPair)
        }

        return keyPair
    }

    actual override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray {
        val result = CryptoJs.encrypt(plainMessage, recipientPublicKey, senderPrivateKey)

        console.log("=== KOTLIN RECEIVED ENCRYPT RESULT ===")
        console.log("Raw result:", result)

        val resultArray: IntArray = result.unsafeCast<IntArray>()
        val length = resultArray.size
        console.log("Parsed length:", length)

        val byteArray = ByteArray(length) { i ->
            (resultArray[i] and 0xFF).toByte()
        }

        console.log("=== FINAL KOTLIN BYTEARRAY ===")
        console.log("byteArray size:", byteArray.size)
        console.log("byteArray[0]:", byteArray.getOrNull(0))
        console.log("byteArray[1]:", byteArray.getOrNull(1))

        // Untuk menampilkan content, konversi ke Int dulu biar positif
        val displayContent = byteArray.map { it.toInt() and 0xFF }
        console.log("byteArray content (unsigned):", displayContent.toIntArray())

        return byteArray
    }

    actual override suspend fun decrypt(
        encryptedData: ByteArray,
        recipientPrivateKey: String
    ): String {
        console.log("=== KOTLIN DECRYPT ===")
        console.log("encryptedData size:", encryptedData.size)

        // Convert ByteArray ke IntArray (unsigned values)
        val intArray = IntArray(encryptedData.size) { i ->
            encryptedData[i].toInt() and 0xFF
        }

        console.log("Converted to Int array:", intArray)
        val jsArray: dynamic = intArray.unsafeCast<dynamic>()
        return CryptoJs.decrypt(jsArray, recipientPrivateKey)
    }


}