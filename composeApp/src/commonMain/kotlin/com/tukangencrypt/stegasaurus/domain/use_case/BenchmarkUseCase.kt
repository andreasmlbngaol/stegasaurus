package com.tukangencrypt.stegasaurus.domain.use_case

import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult
import com.tukangencrypt.stegasaurus.domain.repository.BenchmarkRepository
import kotlinx.coroutines.delay

class BenchmarkUseCase(
    private val repository: BenchmarkRepository
) {
    suspend operator fun invoke(iterations: Int): List<BenchmarkResult> {
        // WARMUP PHASE - Jalankan dulu untuk pemanasan JVM
        // Hasil warmup tidak disimpan
        val warmUpIterations = 1000
        println("Starting warmup phase...")
        repository.benchmarkKeyGeneration(warmUpIterations)
        repository.benchmarkSharedSecret(warmUpIterations)
        repository.benchmarkKeyDerivation(warmUpIterations)
        repository.benchmarkEncryption(warmUpIterations)
        println("Warmup complete!")

        // Small delay untuk GC settle
        delay(100L)
        // ACTUAL BENCHMARK - Ini yang dihitung dan disimpan
        println("Starting actual benchmark with $iterations iterations...")
        val results = listOf(
            repository.benchmarkKeyGeneration(iterations),
            repository.benchmarkSharedSecret(iterations),
            repository.benchmarkKeyDerivation(iterations),
            repository.benchmarkEncryption(iterations)
        )

        results.forEach { println(it) }
        return results
    }
}