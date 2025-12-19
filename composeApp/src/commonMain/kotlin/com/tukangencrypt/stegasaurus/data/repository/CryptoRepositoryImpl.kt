package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository

expect class CryptoRepositoryImpl(
    keyRepository: KeyRepository
) : CryptoRepository {
    override suspend fun generateKeyPair(): KeyPair
    override suspend fun encrypt(
        plainMessage: String,
        recipientPublicKey: String,
        senderPrivateKey: String,
        senderPublicKey: String
    ): ByteArray

    override suspend fun decrypt(
        encryptedData: ByteArray,
        recipientPrivateKey: String
    ): String
}