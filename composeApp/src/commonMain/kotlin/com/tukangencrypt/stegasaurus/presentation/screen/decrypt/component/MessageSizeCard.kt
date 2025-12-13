package com.tukangencrypt.stegasaurus.presentation.screen.decrypt.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tukangencrypt.stegasaurus.utils.value
import stegasaurus.composeapp.generated.resources.Res
import stegasaurus.composeapp.generated.resources.decrypt_message_length_placeholder
import stegasaurus.composeapp.generated.resources.decrypt_message_length_title

@Composable
fun MessageSizeCard(
    messageSize: String,
    onMessageSizeChange: (String) -> Unit,
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
                text = Res.string.decrypt_message_length_title.value,
                style = MaterialTheme.typography.titleMedium
            )

            val containerColor = MaterialTheme.colorScheme.tertiaryContainer
            val contentColor = MaterialTheme.colorScheme.onBackground

            OutlinedTextField(
                value = messageSize,
                onValueChange = onMessageSizeChange,
                placeholder = { Text(Res.string.decrypt_message_length_placeholder.value) },
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
