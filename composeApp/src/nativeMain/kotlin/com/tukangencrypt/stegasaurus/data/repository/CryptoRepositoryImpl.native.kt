package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository

actual class CryptoRepositoryImpl actual constructor(keyRepository: KeyRepository) : CryptoRepository {
    actual override suspend fun generateKeyPair(): KeyPair {
        TODO("Not yet implemented")
    }

    actual override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String
    ): ByteArray {
        TODO("Not yet implemented")
    }

    actual override suspend fun decrypt(
        encryptedData: ByteArray,
        senderPublicKey: String,
        recipientPrivateKey: String
    ): String {
        TODO("Not yet implemented")
    }
}