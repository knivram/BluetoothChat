package com.itsallprivate.bluetoothchat.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import com.itsallprivate.bluetoothchat.presentation.viewmodels.BluetoothViewModel

@Composable
fun DeviceList(
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<BluetoothViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            item {
                Title("Paired Devices")
            }
            items(state.pairedDevices.toList()) { device ->
                DeviceItem(
                    device = device,
                    onClick = { onClick(device) },
                )
            }

            item {
                Title("Scanned Devices")
            }
            if (state.scannedDevices.isEmpty()) {
                item {
                    Text(
                        text = "No devices found",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
                items(state.scannedDevices.sortedByDescending { it.name }) { device ->
                    DeviceItem(
                        device = device,
                        onClick = { onClick(device) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Title(
    text: String,
) {
    Text(
        text = text,
        color = Color.DarkGray,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun DeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = device.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = device.address,
            fontSize = 12.sp,
            color = Color.DarkGray,
        )
    }
}
