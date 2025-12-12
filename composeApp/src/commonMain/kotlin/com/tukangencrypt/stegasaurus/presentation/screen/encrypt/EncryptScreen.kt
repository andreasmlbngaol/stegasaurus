@file:Suppress("DEPRECATION")

package com.tukangencrypt.stegasaurus.presentation.screen.encrypt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tukangencrypt.stegasaurus.presentation.component.TopBar
import com.tukangencrypt.stegasaurus.presentation.component.calculateWindowSize
import com.tukangencrypt.stegasaurus.presentation.screen.encrypt.component.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.deprecated.openFileSaver
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun EncryptScreen(
    modifier: Modifier = Modifier,
    viewModel: EncryptViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val windowSizeClass = calculateWindowSize()
    val isCompactWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.isEncrypted) {
        if (state.isEncrypted) {
            snackbarHostState.showSnackbar("Message encrypted successfully!")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier,
        topBar = {
            TopBar(
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
                EncryptTitleSection()

                if (isCompactWidth) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RecipientPublicKeyCard(
                            publicKey = state.recipientPublicKey,
                            onPublicKeyChange = viewModel::onRecipientPublicKeyChanged,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        UploadImageCard(
                            selectedImageSize = state.selectedImageBytes?.size?.toLong() ?: 0L,
                            selectedImageName = state.selectedImageName,
                            onUploadImage = viewModel::pickImage,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading
                        )

                        EncryptMessageCard(
                            message = state.message,
                            onMessageChange = viewModel::onMessageChanged,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        EncryptedImageDownloadCard(
                            enabled = state.embeddedImageBytes != null && !state.isLoading,
                            isLoading = state.isLoading,
                            onDownload = {
                                scope.launch {
                                    FileKit.openFileSaver(state.embeddedImageBytes, "stega_encrypted", "png")
                                }
                            },
                            hasEncryptedImage = state.embeddedImageBytes != null,
                            encryptedImageSize = state.embeddedImageBytes?.size?.toLong(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            RecipientPublicKeyCard(
                                publicKey = state.recipientPublicKey,
                                onPublicKeyChange = viewModel::onRecipientPublicKeyChanged,
                                modifier = Modifier
                                    .fillMaxWidth(),
                            )

                            UploadImageCard(
                                selectedImageSize = state.selectedImageBytes?.size?.toLong() ?: 0L,
                                selectedImageName = state.selectedImageName,
                                onUploadImage = viewModel::pickImage,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                enabled = !state.isLoading
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            EncryptMessageCard(
                                message = state.message,
                                onMessageChange = viewModel::onMessageChanged,
                                modifier = Modifier
                                    .fillMaxWidth(),
                            )

                            EncryptedImageDownloadCard(
                                enabled = state.embeddedImageBytes != null && !state.isLoading,
                                isLoading = state.isLoading,
                                onDownload = {
                                    scope.launch {
                                        FileKit.openFileSaver(state.embeddedImageBytes, "stega_encrypted", "png")
                                    }
                                },
                                hasEncryptedImage = state.embeddedImageBytes != null,
                                encryptedImageSize = state.embeddedImageBytes?.size?.toLong(),
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.size(16.dp))

            Button(
                onClick = viewModel::encryptAndEmbed,
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxWidth(),
                shapes = ButtonDefaults.shapes(),
                enabled = state.encryptButtonEnabled && !state.isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Encrypt"
                        )
                    }

                    Text(if (state.isLoading) "Encrypting..." else "Encrypt & Hide Message")
                }
            }
        }
    }
}