package com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.presentation.component.IconCard
import com.tukangencrypt.stegasaurus.utils.formatFileSize

@Composable
fun UploadEncryptedImageCard(
    selectedImageName: String?,
    selectedImageSize: Long?,
    onUploadImage: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Select Encrypted Image",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable {
                        if (!enabled) return@clickable
                        onUploadImage()
                    }
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCard(
                    imageVector = Icons.Outlined.Upload,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    onClick = onUploadImage,
                    enabled = true
                )

                AnimatedContent(selectedImageName) { imageName ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (imageName == null) {
                            Text(
                                text = "Upload Encrypted Image",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "PNG, JPG, up to 10MB",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = imageName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Click to change image",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (selectedImageSize != null) {
                                Text(
                                    text = formatFileSize(selectedImageSize),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
