package com.itsallprivate.bluetoothchat.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.itsallprivate.bluetoothchat.BluetoothConnection
import com.itsallprivate.bluetoothchat.Chat
import com.itsallprivate.bluetoothchat.presentation.components.ChatOverviewItem
import com.itsallprivate.bluetoothchat.presentation.viewmodels.ChatsOverviewViewModel

@Composable
fun ChatsOverviewScreen(
    navController: NavHostController,
) {
    val viewModel = hiltViewModel<ChatsOverviewViewModel>()
    val chats by viewModel.chats.collectAsState()

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Chats",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = {
                            navController.navigate(BluetoothConnection)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                LazyColumn {
                    items(chats) { chat ->
                        ChatOverviewItem(
                            chatOverview = chat,
                            onClick = { navController.navigate(Chat(chat.name, chat.address)) },
                        )
                    }
                }
            }
        }
    }
}
