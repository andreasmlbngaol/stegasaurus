package com.tukangencrypt.stegasaurus.presentation.screen.decrypt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tukangencrypt.stegasaurus.presentation.component.TopBar
import com.tukangencrypt.stegasaurus.presentation.component.calculateWindowSize
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component.DecryptTitleSection
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component.DecryptedMessageCard
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component.MessageSizeCard
import com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component.UploadEncryptedImageCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DecryptScreen(
    modifier: Modifier = Modifier,
    viewModel: DecryptViewModel = koinViewModel()
) {
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

    LaunchedEffect(state.isDecrypted) {
        if (state.isDecrypted) {
            snackbarHostState.showSnackbar("Message decrypted successfully!")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier,
        topBar = {
            TopBar(
                containerColor = Color.Transparent,
                scrollBehavior = scrollBehavior
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
                DecryptTitleSection()

                if(isCompactWidth) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        UploadEncryptedImageCard(
                            selectedImageSize = state.selectedImageBytes?.size?.toLong() ?: 0L,
                            selectedImageName = state.selectedImageName,
                            onUploadImage = viewModel::pickImage,
                            modifier = Modifier
                                .fillMaxWidth(),
                            enabled = !state.isLoading
                        )

                        MessageSizeCard(
                            messageSize = state.messageSize,
                            onMessageSizeChange = viewModel::onMessageSizeChanged,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )

                        DecryptedMessageCard(
                            decryptedMessage = state.decryptedMessage,
                            hasDecryptedMessage = state.decryptedMessage != null,
                            isLoading = state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
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
                            UploadEncryptedImageCard(
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
                            MessageSizeCard(
                                messageSize = state.messageSize,
                                onMessageSizeChange = viewModel::onMessageSizeChanged,
                                modifier = Modifier
                                    .fillMaxWidth(),
                            )

                            DecryptedMessageCard(
                                decryptedMessage = state.decryptedMessage,
                                hasDecryptedMessage = state.decryptedMessage != null,
                                isLoading = state.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.size(16.dp))

            Button(
                onClick = viewModel::extractAndDecrypt,
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxWidth(),
                shapes = ButtonDefaults.shapes(),
                enabled = state.decryptButtonEnabled && !state.isLoading
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Decrypt"
                        )
                    }

                    Text(if (state.isLoading) "Decrypting..." else "Extract & Decrypt Message")
                }
            }
        }
    }
}
