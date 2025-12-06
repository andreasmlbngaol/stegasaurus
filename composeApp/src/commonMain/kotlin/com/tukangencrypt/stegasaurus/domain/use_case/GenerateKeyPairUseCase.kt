package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.repository.CryptoRepository

/**
 * Generate key pair untuk crypto
 */
class GenerateKeyPairUseCase(
    private val cryptoRepository: CryptoRepository
) {
    suspend operator fun invoke() = cryptoRepository.generateKeyPair()
}
