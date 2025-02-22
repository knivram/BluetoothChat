package com.example.bluetooth_chat.presentation

import com.example.bluetooth_chat.domain.chat.BluetoothDevice
import com.example.bluetooth_chat.domain.chat.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: Set<BluetoothDevice> = emptySet(),
    val pairedDevices: Set<BluetoothDevice> = emptySet(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList(),
)
