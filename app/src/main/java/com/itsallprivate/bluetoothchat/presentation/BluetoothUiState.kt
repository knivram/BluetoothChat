package com.itsallprivate.bluetoothchat.presentation

import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: Set<BluetoothDevice> = emptySet(),
    val pairedDevices: Set<BluetoothDevice> = emptySet(),
)
