package com.tukangencrypt.stegasaurus.presentation.screen.encrypt.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.Center

@Composable
fun EncryptedImageDownloadCard(
    enabled: Boolean,
    isLoading: Boolean = false,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        AnimatedContent(isLoading) { loading ->
            if(!loading) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Encrypted Image",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(
                        enabled = enabled,
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = "Download"
                            )
                            Text("Download Image")
                        }
                    }
                }
            } else {
                Center(modifier = Modifier.padding(24.dp)) { CircularProgressIndicator() }
            }
        }
    }
}