package com.tukangencrypt.stegasaurus.presentation.screen.benchmark

import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult

data class BenchmarkState(
    val isRunning: Boolean = false,
    val results: List<BenchmarkResult> = emptyList(),
    val selectedIterations: Int = 100,
    val errorMessage: String? = null
)