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
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.utils.formatFileSize
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.encrypt_download_button_text
import stegasaurus.composeapp.generated.resources.encrypt_download_subtitle
import stegasaurus.composeapp.generated.resources.encrypt_download_title

@Composable
fun EncryptedImageDownloadCard(
    enabled: Boolean,
    isLoading: Boolean = false,
    hasEncryptedImage: Boolean = false,
    encryptedImageSize: Long? = null,
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
        AnimatedContent(hasEncryptedImage) { encrypted ->
            if(!encrypted) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconCard(
                        imageVector = Icons.Outlined.Download,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                        enabled = false
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = Res.string.encrypt_download_title.value,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = Res.string.encrypt_download_subtitle.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                if(!isLoading) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = Res.string.encrypt_download_title.value,
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
                                Text(Res.string.encrypt_download_button_text.value)
                            }
                        }

                        if (encryptedImageSize != null) {
                            Text(
                                text = formatFileSize(encryptedImageSize),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Center(modifier = Modifier.padding(24.dp)) { CircularProgressIndicator() }
                }
            }
        }
    }
}
