package com.itsallprivate.bluetoothchat.presentation.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsallprivate.bluetoothchat.presentation.BluetoothViewModel

@Composable
fun BluetoothConnectionScreen(
    applicationContext: Context = LocalContext.current.applicationContext
) {
    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.errorMessage) {
        state.errorMessage?.let { message ->
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(key1 = state.isConnected) {
        if(state.isConnected) {
            Toast.makeText(
                applicationContext,
                "You're connected!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                when {
                    state.isConnecting -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Connecting...")
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = viewModel::disconnectFromDevice) {
                                Text("Cancel")
                            }
                        }
                    }
                    state.isConnected -> {
                        ChatScreen(
                            state = state,
                            onSendMessage = viewModel::sendMessage,
                            onDisconnect = viewModel::disconnectFromDevice
                        )
                    }
                    else -> {
                        DeviceScreen(
                            state = state,
                            onStartScan = viewModel::startScan,
                            onStopScan = viewModel::stopScan,
                            onDeviceClick = viewModel::connectToDevice,
                            onStartServer = viewModel::waitForIncomingConnections
                        )
                    }
                }
            }
        }
    }
}