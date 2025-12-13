package com.tukangencrypt.stegasaurus.presentation.screen.encrypt.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.recipient_public_key_label
import stegasaurus.composeapp.generated.resources.recipient_public_key_placeholder

@Composable
fun RecipientPublicKeyCard(
    publicKey: String,
    onPublicKeyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
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
                text = Res.string.recipient_public_key_label.value,
                style = MaterialTheme.typography.titleMedium
            )

            val containerColor = MaterialTheme.colorScheme.primaryContainer
            val contentColor = MaterialTheme.colorScheme.onBackground

            OutlinedTextField(
                value = publicKey,
                onValueChange = onPublicKeyChange,
                placeholder = { Text(Res.string.recipient_public_key_placeholder.value) },
                colors = OutlinedTextFieldDefaults.colors().copy(
                    unfocusedContainerColor = containerColor,
                    focusedContainerColor = containerColor,
                    unfocusedTextColor = contentColor,
                    focusedTextColor = contentColor
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}