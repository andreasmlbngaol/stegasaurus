package com.tukangencrypt.stegasaurus.presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.presentation.screen.home.component.HomeFloatingActionButton
import com.tukangencrypt.stegasaurus.presentation.screen.home.component.HomeGenerateKeyCard
import com.tukangencrypt.stegasaurus.presentation.screen.home.component.HomeInfoCards
import com.tukangencrypt.stegasaurus.presentation.screen.home.component.HomeMainMenu
import com.tukangencrypt.stegasaurus.presentation.screen.home.component.PublicKeyDialog
import com.tukangencrypt.stegasaurus.utils.toClipEntry
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    onNavigateToEncrypt: () -> Unit,
    onNavigateToDecrypt: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(state.keyPairExists) {
                HomeFloatingActionButton(viewModel::showPubKeyDialog)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconCard(imageVector = Icons.Outlined.Lock)
            Spacer(Modifier.size(16.dp))

            Text(
                text = "Stegasaurus",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(16.dp))

            Text(
                text = "Secure your messages with advanced steganography and encryption technology.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(64.dp))

            AnimatedContent(state.keyPairExists) { exists ->
                if (exists) {
                    HomeMainMenu(
                        onNavigateToEncrypt = onNavigateToEncrypt,
                        onNavigateToDecrypt = onNavigateToDecrypt
                    )
                } else {
                    HomeGenerateKeyCard(
                        onGenerateKeyPair = {
                            viewModel.generateKeyPair()
                            viewModel.showPubKeyDialog()
                        }
                    )
                }
            }
            Spacer(Modifier.size(64.dp))

            HomeInfoCards()
            Spacer(Modifier.size(64.dp))

        }

        if(state.pubKeyDialogVisible) {
            PublicKeyDialog(
                publicKey = state.publicKey,
                onDismiss = viewModel::dismissPubKeyDialog
            )
        }
    }
}