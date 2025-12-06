package com.tukangencrypt.stegasaurus.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.TopBar
import com.tukangencrypt.stegasaurus.presentation.component.TopBarType
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.deprecated.openFileSaver
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    var selectedImageName by remember { mutableStateOf<String?>(null) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var messageText by remember { mutableStateOf("") }
    var msgSizeForExtract by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }

    val embedState by viewModel.embedState.collectAsState()
    val extractState by viewModel.extractState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // FileKit launcher untuk pick image
    suspend fun pickImage() {
        val imageFile = FileKit.openFilePicker(type = FileKitType.Image)
        if (imageFile != null) {
            selectedImageName = imageFile.name
            selectedImageBytes = imageFile.readBytes()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                title = "Stegasaurus",
                type = TopBarType.Centered,
                containerColor = Color.Transparent
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tabs
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Embed") }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Extract") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image Picker Section (shared)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select Image",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pickImage()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedImageName == null) "Pick Image" else "Change Image")
                    }

                    if (selectedImageName != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Selected: $selectedImageName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Content
            when (activeTab) {
                0 -> EmbedTabContent(
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    selectedImageBytes = selectedImageBytes,
                    embedState = embedState,
                    isLoading = isLoading,
                    onEmbed = {
                        if (selectedImageBytes != null) {
                            viewModel.embedImage(selectedImageBytes!!, messageText)
                        }
                    },
                    onClearResult = { viewModel.clearEmbedResult() }
                )

                1 -> ExtractTabContent(
                    msgSize = msgSizeForExtract,
                    onMsgSizeChange = { msgSizeForExtract = it },
                    selectedImageBytes = selectedImageBytes,
                    extractState = extractState,
                    isLoading = isLoading,
                    onExtract = {
                        val size = msgSizeForExtract.toIntOrNull()
                        if (selectedImageBytes != null && size != null && size > 0) {
                            viewModel.extractMessage(selectedImageBytes!!, size)
                        }
                    },
                    onClearResult = { viewModel.clearExtractResult() }
                )
            }
        }
    }
}

@Composable
fun Card(modifier: Modifier, colors: Color, content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
private fun EmbedTabContent(
    messageText: String,
    onMessageChange: (String) -> Unit,
    selectedImageBytes: ByteArray?,
    embedState: EmbedState,
    isLoading: Boolean,
    onEmbed: () -> Unit,
    onClearResult: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Message Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Enter Message",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = messageText,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    placeholder = { Text("Enter message to hide...") },
                    maxLines = 5,
                    enabled = !isLoading
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Embed Button
        Button(
            onClick = onEmbed,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageBytes != null && messageText.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Embed Message")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result
        when (embedState) {
            is EmbedState.Success -> {
                EmbedSuccessCard(
                    fileName = "stega_image.png",
                    onClear = onClearResult,
                    onDownload = { bytes ->
                        // Download logic with FileKit
                        scope.launch {
                            FileKit.openFileSaver(bytes, "stega_image", "png")
                        }
                    },
                    bytes = embedState.bytes
                )
            }

            is EmbedState.Error -> {
                ErrorCard(
                    message = embedState.message,
                    onDismiss = onClearResult
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun ExtractTabContent(
    msgSize: String,
    onMsgSizeChange: (String) -> Unit,
    selectedImageBytes: ByteArray?,
    extractState: ExtractState,
    isLoading: Boolean,
    onExtract: () -> Unit,
    onClearResult: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Message Size Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Message Size",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = msgSize,
                    onValueChange = onMsgSizeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter message size in bytes...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Enter the exact size of the hidden message",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Extract Button
        Button(
            onClick = onExtract,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageBytes != null && msgSize.toIntOrNull() != null && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Extract Message")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result
        when (extractState) {
            is ExtractState.Success -> {
                ExtractSuccessCard(
                    message = extractState.message,
                    onClear = onClearResult
                )
            }

            is ExtractState.Error -> {
                ErrorCard(
                    message = extractState.message,
                    onDismiss = onClearResult
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun EmbedSuccessCard(
    fileName: String,
    bytes: ByteArray,
    onClear: () -> Unit,
    onDownload: (ByteArray) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Green.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "✓ Message embedded successfully!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "File size: ${bytes.size} Bytes",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onDownload(bytes) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Download")
                }
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
private fun ExtractSuccessCard(
    message: String,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Green.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "✓ Message extracted successfully!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                SelectionContainer {
                    Text(
                        message,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear")
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "✗ Error",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dismiss")
            }
        }
    }
}
