package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import kotlinx.coroutines.coroutineScope
import org.khronos.webgl.Uint8Array

// ───── JS module imports ─────
@JsModule("@stablelib/x25519")
@JsNonModule
external val X25519: dynamic

@JsModule("@stablelib/hex")
@JsNonModule
external val hex: dynamic

@JsModule("@stablelib/hkdf")
@JsNonModule
external val hkdf: dynamic

@JsModule("@stablelib/sha256")
@JsNonModule
external val sha256: dynamic

@JsModule("@stablelib/chacha20poly1305")
@JsNonModule
external val chacha20: dynamic

actual class CryptoRepositoryImpl actual constructor(private val keyRepository: KeyRepository) : CryptoRepository {

    private fun ByteArray.toUint8Array(): dynamic = Uint8Array(toTypedArray())
    private fun toByteArray(dynamicArr: dynamic): ByteArray {
        val length = dynamicArr.length as Int
        return ByteArray(length) { i -> (dynamicArr[i] as Int).toByte() }
    }

    /** Generate X25519 key pair */
    actual override suspend fun generateKeyPair(): KeyPair {
        val keyPair = X25519.generateKeyPair()

        val publicKey = hex.encode(keyPair.publicKey)
        val privateKey = hex.encode(keyPair.secretKey)

        coroutineScope {
            keyRepository.saveKeyPair(KeyPair(publicKey, privateKey))
        }

        return KeyPair(publicKey, privateKey)
    }

    private fun clampSecretKey(key: Uint8Array) {
        val dyn = key.asDynamic()

        dyn[0] = (dyn[0] as Int and 248)
        dyn[31] = ((dyn[31] as Int and 127) or 64)
    }

    /** Derive shared secret using X25519 */
    private fun sharedSecret(myPrivHex: String, otherPubHex: String): ByteArray {
        println("Derive shared secret using X25519")
//        val senderPriv = hex.decode(senderPrivHex) as Uint8Array
//        val recipientPub = hex.decode(recipientPubHex) as Uint8Array
        val myPriv = hex.decode(myPrivHex)
        clampSecretKey(myPriv)


        val otherPub = hex.decode(otherPubHex)

        val otherPubHex = hex.encode(otherPub)
        console.log("otherPubHex:", otherPubHex)

        console.log("senderPriv:", myPriv)
        console.log("recipientPub:", otherPub)

        val shared = X25519.sharedKey(myPriv, otherPub)
        val sharedHex = hex.encode(shared)
        console.log("sharedHex:", sharedHex)
        return dynamicToByteArray(shared)
    }

    /** Derive encryption key + nonce using HKDF-SHA256 */
    private fun deriveKeyAndNonce(sharedSecret: ByteArray): Pair<ByteArray, ByteArray> {
        println("Derive key and nonce using HKDF-SHA256")

        val info = "stegasaurus-encryption".encodeToByteArray().toUint8Array()
        val secretArr = sharedSecret.toUint8Array()

        // Langsung buat SHA256 constructor
        val SHA256Ctor = sha256.SHA256

        // Langsung buat HKDF constructor dengan parameter
        val HKDFCtor = hkdf.HKDF
        val hkdfInstance = js("new HKDFCtor(SHA256Ctor, secretArr, undefined, info)")

        console.log("hkdfInstance:", hkdfInstance)

        val keyResult = hkdfInstance.expand(32)
        val nonceResult = hkdfInstance.expand(12)

        console.log("keyResult:", keyResult)
        console.log("nonceResult:", nonceResult)

        val keyArr = dynamicToByteArray(keyResult)
        val nonceArr = dynamicToByteArray(nonceResult)

        return keyArr to nonceArr
    }

    /** Encrypt using ChaCha20-Poly1305 */
    actual override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray {
        val shared = sharedSecret(senderPrivateKey, recipientPublicKey)
        val (key, nonce) = deriveKeyAndNonce(shared)

        val ChaCha20Poly1305Ctor = chacha20.ChaCha20Poly1305
        val cipher = js("new ChaCha20Poly1305Ctor(key)")

        val message = plainMessage.encodeToByteArray().toUint8Array()
        val ciphertext = cipher.seal(nonce.toUint8Array(), message)

        return toByteArray(ciphertext)
    }

    actual override suspend fun decrypt(
        encryptedData: ByteArray,
        recipientPrivateKey: String
    ): String {
        TODO("Not yet implemented")
    }

    /** Decrypt using ChaCha20-Poly1305 */
    actual override suspend fun decrypt(
        encryptedData: ByteArray,
        senderPublicKey: String,
        recipientPrivateKey: String
    ): String {
        val shared = sharedSecret(recipientPrivateKey, senderPublicKey)
        val (key, nonce) = deriveKeyAndNonce(shared)

        val ChaCha20Poly1305Ctor = chacha20.ChaCha20Poly1305
        val cipher = js("new ChaCha20Poly1305Ctor(key)")

        val ciphertext = encryptedData.toUint8Array()
        val plaintext = cipher.open(nonce.toUint8Array(), ciphertext) ?: throw IllegalStateException("Decryption failed")

        return toByteArray(plaintext).decodeToString()
    }
}

private fun dynamicToByteArray(dynamicArr: dynamic): ByteArray {
    val length = dynamicArr.length as Int
    return ByteArray(length) { i -> (dynamicArr[i] as Int).toByte() }
}
