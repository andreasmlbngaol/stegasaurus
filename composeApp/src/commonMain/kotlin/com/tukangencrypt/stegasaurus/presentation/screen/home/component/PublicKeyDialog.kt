package com.tukangencrypt.stegasaurus.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tukangencrypt.stegasaurus.utils.toClipEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PublicKeyDialog(
    publicKey: String,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledIconButton(
                    onClick = {},
                    colors = IconButtonDefaults.filledIconButtonColors().copy(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "View Public Key"
                    )
                }

                Text(
                    text = "Your Public Key",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .weight(1f)
                )

                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close"
                    )
                }
            }

            HorizontalDivider(Modifier.fillMaxWidth())

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Share this public key with people who want to send you encrypted messages.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = publicKey.lowercase(),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        FilledIconButton(
                            onClick = {
                                scope.launch {
                                    clipboard.setClipEntry(publicKey.lowercase().toClipEntry())
                                }
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileCopy,
                                contentDescription = "Copy to Clipboard"
                            )
                        }
                    }
                }

                Button(
                    shapes = ButtonDefaults.shapes(),
                    onClick = {
                        scope.launch {
                            clipboard.setClipEntry(publicKey.lowercase().toClipEntry())
                        }
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FileCopy,
                            contentDescription = "Copy to Clipboard"
                        )
                        Text(
                            text = "Copy to Clipboard"
                        )
                    }
                }
            }
        }
    }
}
