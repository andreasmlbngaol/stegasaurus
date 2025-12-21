package com.tukangencrypt.stegasaurus.domain.repository

import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult

interface BenchmarkRepository {
    fun benchmarkKeyGeneration(iterations: Int): BenchmarkResult
    fun benchmarkSharedSecret(iterations: Int): BenchmarkResult
    fun benchmarkKeyDerivation(iterations: Int): BenchmarkResult
    fun benchmarkEncryption(iterations: Int): BenchmarkResult
}