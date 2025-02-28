package com.itsallprivate.bluetoothchat.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itsallprivate.bluetoothchat.presentation.viewmodels.ConnectionStatus

@Composable
fun ChatTopBar(
    title: String,
    status: ConnectionStatus,
    onProfile: () -> Unit,
    onBackAction: () -> Unit,
    onDisconnect: () -> Unit,
    onConnect: () -> Unit,
    onMakeDiscoverable: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(
                onClick = onBackAction,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onProfile),
            ) {
                ProfilePicture(
                    name = title,
                    size = 38,
                )
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            when (status) {
                ConnectionStatus.CONNECTING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        strokeWidth = 2.dp,
                    )
                    Button(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                    ) {
                        Text("Cancel")
                    }
                }

                ConnectionStatus.CONNECTED -> {
                    Button(
                        onClick = onDisconnect,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                    ) {
                        Text("Disconnect")
                    }
                }

                ConnectionStatus.DISCOVERABLE -> {
                    Button(
                        onClick = onConnect,
                    ) {
                        Text("Connect")
                    }
                }

                ConnectionStatus.DISCONNECTED -> {
                    Button(
                        onClick = onMakeDiscoverable,
                    ) {
                        Text("Make discoverable")
                    }
                }
            }
        }
    }
}
