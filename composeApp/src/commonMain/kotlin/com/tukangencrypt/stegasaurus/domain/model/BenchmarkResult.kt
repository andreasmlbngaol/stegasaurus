package com.tukangencrypt.stegasaurus.domain.model

import com.tukangencrypt.stegasaurus.utils.format

data class BenchmarkResult(
    val operationName: String,
    val iterations: Int,
    val totalTimeMs: Double,
    val averageTimeMs: Double,
    val averageTimeNs: Double
) {
    override fun toString(): String {
        return """
            |$operationName:
            |  Iterations: $iterations
            |  Total: ${totalTimeMs.format(3)} ms
            |  Average: ${averageTimeMs.format(6)} ms (${averageTimeNs.format(3)} μs)
        """.trimMargin()
    }
}