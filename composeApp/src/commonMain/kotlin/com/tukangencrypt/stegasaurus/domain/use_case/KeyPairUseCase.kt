package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository
import com.tukangencrypt.stegasaurus.domain.repository.KeyRepository

class KeyPairUseCase(
    private val keyRepository: KeyRepository,
    private val cryptoRepository: CryptoRepository
) {
    suspend fun keyPairExists() = keyRepository.hasKeyPair()
    fun getPublicKey() = keyRepository.getPublicKey()

    fun getPrivateKey() = keyRepository.getPrivateKey()

    suspend fun generateKeyPair(): KeyPair {
        val keyPair = cryptoRepository.generateKeyPair()
        keyRepository.saveKeyPair(keyPair)
        return keyPair
    }
}