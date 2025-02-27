package com.itsallprivate.bluetoothchat.presentation.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.itsallprivate.bluetoothchat.Profile
import com.itsallprivate.bluetoothchat.presentation.components.ChatMessage
import com.itsallprivate.bluetoothchat.presentation.components.ChatTopBar
import com.itsallprivate.bluetoothchat.presentation.viewmodels.ChatViewModel
import com.itsallprivate.bluetoothchat.presentation.viewmodels.ConnectionStatus

@Composable
fun ChatScreen(
    navController: NavHostController,
) {
    val viewModel = hiltViewModel<ChatViewModel>()
    val messages by viewModel.messages.collectAsState()
    val status by viewModel.status.collectAsState()
    val device = viewModel.device

    var message by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // Ensure only the chat list shrinks when the keyboard shows,
    // keeping the top bar fixed.
    DisposableEffect(context) {
        val activity = context as? Activity
        val originalSoftInputMode = activity?.window?.attributes?.softInputMode
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        onDispose {
            activity?.window?.setSoftInputMode(
                originalSoftInputMode ?: WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
            )
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                ChatTopBar(
                    title = device.name,
                    status = status,
                    onBackAction = {
                        viewModel.disconnectFromDevice()
                        navController.popBackStack()
                    },
                    onProfile = {
                        navController.navigate(Profile(device.address))
                    },
                    onDisconnect = viewModel::disconnectFromDevice,
                    onConnect = viewModel::connectConnectToDevice,
                    onMakeDiscoverable = viewModel::waitForIncomingConnections,
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    reverseLayout = true,
                ) {
                    items(messages) { message ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ChatMessage(
                                message = message,
                                modifier = Modifier.align(
                                    if (message.isFromLocalUser) Alignment.End else Alignment.Start,
                                ),
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message") },
                    )

                    IconButton(
                        onClick = {
                            viewModel.sendMessage(message)
                            keyboardController?.hide()
                            message = ""
                        },
                        enabled = message.isNotBlank() && status == ConnectionStatus.CONNECTED,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Send,
                            contentDescription = "Send",
                        )
                    }
                }
            }
        }
    }
}
