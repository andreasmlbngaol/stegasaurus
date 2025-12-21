package com.tukangencrypt.stegasaurus.data.repository

import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult
import com.tukangencrypt.stegasaurus.domain.repository.BenchmarkRepository

actual class BenchmarkRepositoryImpl : BenchmarkRepository {
    actual override fun benchmarkKeyGeneration(iterations: Int): BenchmarkResult {
        TODO("Not yet implemented")
    }

    actual override fun benchmarkSharedSecret(iterations: Int): BenchmarkResult {
        TODO("Not yet implemented")
    }

    actual override fun benchmarkKeyDerivation(iterations: Int): BenchmarkResult {
        TODO("Not yet implemented")
    }

    actual override fun benchmarkEncryption(iterations: Int): BenchmarkResult {
        TODO("Not yet implemented")
    }
}