package com.itsallprivate.bluetoothchat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsallprivate.bluetoothchat.presentation.BluetoothUiState
import com.itsallprivate.bluetoothchat.presentation.BluetoothViewModel
import com.itsallprivate.bluetoothchat.presentation.ChatViewModel
import com.itsallprivate.bluetoothchat.presentation.ConnectionStatus

@Composable
fun ChatScreen() {
    val viewModel = hiltViewModel<ChatViewModel>()
    val messages by viewModel.messages.collectAsState()
    val status by viewModel.status.collectAsState()
    val device = viewModel.device

    var message by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = device.name ?: "Unknown Device",
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        when (status) {
                            ConnectionStatus.CONNECTING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Button(viewModel::disconnectFromDevice) {
                                    Text("Cancel")
                                }
                            }

                            ConnectionStatus.CONNECTED -> {
                                Button(onClick = viewModel::disconnectFromDevice) {
                                    Text("Disconnect")
                                }
                            }

                            ConnectionStatus.DISCONNECTED -> {
                                Button(onClick = viewModel::connectConnectToDevice) {
                                    Text("Connect")
                                }
                                Button(onClick = viewModel::waitForIncomingConnections) {
                                    Text("Server")
                                }
                            }
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { message ->
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ChatMessage(
                                message = message,
                                modifier = Modifier.align(
                                    if (message.isFromLocalUser) Alignment.End else Alignment.Start
                                )
                            )
                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message") }
                    )

                    IconButton(onClick = {
                        viewModel.sendMessage(message)
                        keyboardController?.hide()
                        message = ""
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }
    }
}
