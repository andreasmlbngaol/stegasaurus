package com.tukangencrypt.stegasaurus.presentation.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.domain.model.KeyPair
import com.tukangencrypt.stegasaurus.presentation.component.TopBar
import com.tukangencrypt.stegasaurus.presentation.component.TopBarType
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.deprecated.openFileSaver
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
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
    var recipientPublicKey by remember { mutableStateOf("") }
    var senderPublicKey by remember { mutableStateOf("") }
    var messageSizeForExtract by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }

    val keyState by viewModel.keyState.collectAsState()
    val embedState by viewModel.embedState.collectAsState()
    val extractState by viewModel.extractState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val myKeyPair by viewModel.myKeyPair.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    suspend fun pickImage() {
        val imageFile = FileKit.openFilePicker(type = FileKitType.Image)
        if (imageFile != null) {
            selectedImageName = imageFile.name
            selectedImageBytes = imageFile.readBytes()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                title = "Stegasaurus",
                type = TopBarType.Centered,
                containerColor = Color.Transparent,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Key Management Section
            KeyManagementSection(
                myKeyPair = myKeyPair,
                isLoading = isLoading,
                onGenerateKey = { viewModel.generateMyKeyPair() },
                recipientPublicKey = recipientPublicKey,
                onRecipientKeyChange = {
                    recipientPublicKey = it
                    viewModel.setRecipientPublicKey(it)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Encrypt & Embed") }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Extract & Decrypt") }
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
                0 -> EncryptEmbedTabContent(
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    selectedImageBytes = selectedImageBytes,
                    embedState = embedState,
                    isLoading = isLoading,
                    onEncryptEmbed = {
                        if (selectedImageBytes != null) {
                            viewModel.encryptAndEmbed(selectedImageBytes!!, messageText)
                        }
                    },
                    onClearResult = { viewModel.clearEmbedResult() },
                    coroutineScope = coroutineScope
                )

                1 -> ExtractDecryptTabContent(
                    senderPublicKey = senderPublicKey,
                    onSenderKeyChange = { senderPublicKey = it },
                    messageSizeForExtract = messageSizeForExtract,
                    onMessageSizeChange = { messageSizeForExtract = it },
                    selectedImageBytes = selectedImageBytes,
                    extractState = extractState,
                    isLoading = isLoading,
                    onExtractDecrypt = {
                        val size = messageSizeForExtract.toIntOrNull()
                        if (selectedImageBytes != null && size != null && size > 0) {
                            viewModel.extractAndDecrypt(selectedImageBytes!!, size, senderPublicKey)
                        }
                    },
                    onClearResult = { viewModel.clearExtractResult() }
                )
            }
        }
    }
}

@Composable
private fun KeyManagementSection(
    myKeyPair: KeyPair?,
    isLoading: Boolean,
    onGenerateKey: () -> Unit,
    recipientPublicKey: String,
    onRecipientKeyChange: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Key Management",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Generate Key Button
            Button(
                onClick = onGenerateKey,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Generate My Key Pair")
            }

            // My Public Key Display
            if (myKeyPair != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "My Public Key:",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SelectionContainer(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    myKeyPair.publicKey,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                            }

                            val platformContext = androidx.compose.ui.platform.LocalClipboardManager.current

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        // Copy to clipboard
                                        platformContext.setText(
                                            androidx.compose.ui.text.AnnotatedString(myKeyPair.publicKey)
                                        )
                                        snackbarHostState.showSnackbar(
                                            "Public key copied to clipboard",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy"
                                )
                            }
                        }
                    }
                }
            }

            // Recipient Public Key Input
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = recipientPublicKey,
                onValueChange = onRecipientKeyChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                label = { Text("Recipient Public Key") },
                placeholder = { Text("Paste recipient's public key here...") },
                maxLines = 3,
                enabled = !isLoading
            )
        }
    }
}

@Composable
private fun EncryptEmbedTabContent(
    messageText: String,
    onMessageChange: (String) -> Unit,
    selectedImageBytes: ByteArray?,
    embedState: EmbedState,
    isLoading: Boolean,
    onEncryptEmbed: () -> Unit,
    onClearResult: () -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
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
                    "Message to Hide",
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

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Length: ${messageText.length} characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Encrypt & Embed Button
        Button(
            onClick = onEncryptEmbed,
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
            Text("Encrypt & Embed")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result
        when (embedState) {
            is EmbedState.Success -> {
                EncryptEmbedSuccessCard(
                    bytes = embedState.bytes,
                    onClear = onClearResult,
                    onDownload = { bytes ->
                        coroutineScope.launch {
                            FileKit.openFileSaver(bytes, "stega_encrypted", "png")
                        }
                    }
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
private fun ExtractDecryptTabContent(
    senderPublicKey: String,
    onSenderKeyChange: (String) -> Unit,
    messageSizeForExtract: String,
    onMessageSizeChange: (String) -> Unit,
    selectedImageBytes: ByteArray?,
    extractState: ExtractState,
    isLoading: Boolean,
    onExtractDecrypt: () -> Unit,
    onClearResult: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Sender Public Key Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Sender's Public Key",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = senderPublicKey,
                    onValueChange = onSenderKeyChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    placeholder = { Text("Paste sender's public key here...") },
                    maxLines = 3,
                    enabled = !isLoading
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message Size Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Encrypted Message Size",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = messageSizeForExtract,
                    onValueChange = onMessageSizeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter size in bytes...") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This is the encrypted message size (original + encryption overhead)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Extract & Decrypt Button
        Button(
            onClick = onExtractDecrypt,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedImageBytes != null &&
                    messageSizeForExtract.toIntOrNull() != null &&
                    !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Extract & Decrypt")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result
        when (extractState) {
            is ExtractState.Success -> {
                ExtractDecryptSuccessCard(
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
private fun EncryptEmbedSuccessCard(
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
                "✓ Message Encrypted & Embedded!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "File size: ${bytes.size} bytes",
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
private fun ExtractDecryptSuccessCard(
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
                "✓ Message Decrypted!",
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
