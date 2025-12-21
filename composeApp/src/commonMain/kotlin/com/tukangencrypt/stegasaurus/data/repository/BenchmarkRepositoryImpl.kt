package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult
import com.tukangencrypt.stegasaurus.domain.repository.BenchmarkRepository

expect class BenchmarkRepositoryImpl(): BenchmarkRepository {
    override fun benchmarkKeyGeneration(iterations: Int): BenchmarkResult
    override fun benchmarkSharedSecret(iterations: Int): BenchmarkResult
    override fun benchmarkKeyDerivation(iterations: Int): BenchmarkResult
    override fun benchmarkEncryption(iterations: Int): BenchmarkResult
}
