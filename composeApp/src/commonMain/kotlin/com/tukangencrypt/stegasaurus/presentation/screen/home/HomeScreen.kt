package com.tukangencrypt.stegasaurus.presentation.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tukangencrypt.stegasaurus.presentation.component.calculateWindowSize
import com.tukangencrypt.stegasaurus.presentation.screen.home.component.*
import com.tukangencrypt.stegasaurus.utils.value
import org.koin.compose.viewmodel.koinViewModel
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.app_name
import stegasaurus.composeapp.generated.resources.home_subtitle
import stegasaurus.composeapp.generated.resources.ic_launcher

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    onNavigateToEncrypt: () -> Unit,
    onNavigateToDecrypt: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val windowSizeClass = calculateWindowSize()
    val isCompactWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

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
            Image(
                painter = Res.drawable.ic_launcher.value,
                contentDescription = "Stegasaurus Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.size(16.dp))

            Text(
                text = Res.string.app_name.value,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(16.dp))

            Text(
                text = Res.string.home_subtitle.value,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(64.dp))

            AnimatedContent(state.keyPairExists) { exists ->
                if (exists) {
                    HomeMainMenu(
                        onNavigateToEncrypt = onNavigateToEncrypt,
                        onNavigateToDecrypt = onNavigateToDecrypt,
                        isCompactWidth = isCompactWidth
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

            HomeInfoCards(isCompactWidth = isCompactWidth)
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