package com.tukangencrypt.stegasaurus.presentation.screen.benchmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tukangencrypt.stegasaurus.domain.model.BenchmarkResult
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.presentation.component.TopBar
import com.tukangencrypt.stegasaurus.presentation.component.calculateWindowSize
import com.tukangencrypt.stegasaurus.presentation.navigation.Navigator
import com.tukangencrypt.stegasaurus.utils.format
import com.tukangencrypt.stegasaurus.utils.value
import org.koin.compose.viewmodel.koinViewModel
import stegasaurus.composeapp.generated.resources.*

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun BenchmarkScreen(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: BenchmarkViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val windowSizeClass = calculateWindowSize()
    val isCompactWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier,
        topBar = {
            TopBar(
                navigator = navigator,
                scrollBehavior = scrollBehavior,
                containerColor = Color.Transparent
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                BenchmarkTitleSection()

                if (isCompactWidth) {
                    BenchmarkCompactScreen(
                        state = state,
                        onSetIterations = viewModel::setIterations,
                        onRunAll = viewModel::runAllBenchmarks,
                        onClearResults = viewModel::clearResults,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BenchmarkExpandedScreen(
                        state = state,
                        onSetIterations = viewModel::setIterations,
                        onRunAll = viewModel::runAllBenchmarks,
                        onClearResults = viewModel::clearResults,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun BenchmarkTitleSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCard(
            imageVector = Icons.Outlined.Speed,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.background,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = Res.string.benchmark_title.value,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = Res.string.benchmark_subtitle.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun BenchmarkCompactScreen(
    state: BenchmarkState,
    onSetIterations: (Int) -> Unit,
    onRunAll: () -> Unit,
    onClearResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IterationSelectorCard(
            selectedIterations = state.selectedIterations,
            onSetIterations = onSetIterations,
            enabled = !state.isRunning,
            modifier = Modifier.fillMaxWidth()
        )

        BenchmarkControlCard(
            isRunning = state.isRunning,
            onRunAll = onRunAll,
            onClearResults = onClearResults,
            hasResults = state.results.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = state.results.isNotEmpty()) {
            BenchmarkResultsCard(
                results = state.results,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BenchmarkExpandedScreen(
    state: BenchmarkState,
    onSetIterations: (Int) -> Unit,
    onRunAll: () -> Unit,
    onClearResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IterationSelectorCard(
                selectedIterations = state.selectedIterations,
                onSetIterations = onSetIterations,
                enabled = !state.isRunning,
                modifier = Modifier.fillMaxWidth()
            )

            BenchmarkControlCard(
                isRunning = state.isRunning,
                onRunAll = onRunAll,
                onClearResults = onClearResults,
                hasResults = state.results.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BenchmarkResultsCard(
                results = state.results,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun IterationSelectorCard(
    selectedIterations: Int,
    onSetIterations: (Int) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = Res.string.iterations_label.value,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(100, 500, 1000).forEach { iter ->
                    FilterChip(
                        selected = selectedIterations == iter,
                        onClick = { onSetIterations(iter) },
                        label = { Text("${iter}x") },
                        enabled = enabled,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BenchmarkControlCard(
    isRunning: Boolean,
    onRunAll: () -> Unit,
    onClearResults: () -> Unit,
    hasResults: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = Res.string.run_benchmark_title.value,
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = onRunAll,
                enabled = !isRunning,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Speed,
                            contentDescription = "Run All"
                        )
                    }
                    Text(
                        if (isRunning)
                            Res.string.running_benchmark_button_text.value
                        else
                            Res.string.run_all_benchmark_button_text.value
                    )
                }
            }

            if (hasResults) {
                HorizontalDivider()

                OutlinedButton(
                    onClick = onClearResults,
                    enabled = !isRunning,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(Res.string.clear_results_button_text.value)
                }
            }
        }
    }
}

@Composable
private fun BenchmarkResultsCard(
    results: List<BenchmarkResult>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = Res.string.results_title.value,
                style = MaterialTheme.typography.titleMedium
            )

            if (results.isEmpty()) {
                Text(
                    text = Res.string.no_benchmark_description.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    results.forEach { result ->
                        BenchmarkResultItem(result = result)
                    }
                    BenchmarkSummaryCard(results = results)
                }
            }
        }
    }
}

@Composable
private fun BenchmarkResultItem(
    result: BenchmarkResult,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = result.operationName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${Res.string.iterations_label.value}:",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${result.iterations}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${Res.string.total_time_label.value}:",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${result.totalTimeMs.format(5)} ms",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${Res.string.average_label.value}:",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${result.averageTimeMs.format(5)} ms",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${Res.string.average_label.value} (ns):",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${result.averageTimeNs.format(2)} ns",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun BenchmarkSummaryCard(
    results: List<BenchmarkResult>,
    modifier: Modifier = Modifier
) {
    val totalTime = results.sumOf { it.totalTimeMs }
    val overallAverage = results.sumOf { it.averageTimeMs }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = Res.string.summary_title.value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Res.string.combined_time_label.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${totalTime.format(5)} ms",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = Res.string.combined_average_label.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${overallAverage.format(5)} ms",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
