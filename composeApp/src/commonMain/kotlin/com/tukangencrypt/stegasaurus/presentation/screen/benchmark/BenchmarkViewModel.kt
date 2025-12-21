package com.tukangencrypt.stegasaurus.presentation.screen.benchmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tukangencrypt.stegasaurus.domain.use_case.BenchmarkUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BenchmarkViewModel(
    private val benchmarkUseCase: BenchmarkUseCase
): ViewModel() {
    private val _state = MutableStateFlow(BenchmarkState())
    val state = _state.asStateFlow()

    fun runAllBenchmarks() {
        viewModelScope.launch(Dispatchers.Default) {
            _state.value = _state.value.copy(
                isRunning = true,
                errorMessage = null
            )

            try {
                val results = benchmarkUseCase(_state.value.selectedIterations)
                _state.value = _state.value.copy(
                    results = results,
                    isRunning = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRunning = false,
                    errorMessage = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun setIterations(iterations: Int) {
        _state.value = _state.value.copy(selectedIterations = iterations)
    }

    fun clearResults() {
        _state.value = _state.value.copy(
            results = emptyList(),
            errorMessage = null
        )
    }

    fun dismissError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}