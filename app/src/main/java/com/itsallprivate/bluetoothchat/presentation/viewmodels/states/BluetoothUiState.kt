package com.itsallprivate.bluetoothchat.presentation.viewmodels.states

import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: Set<BluetoothDevice> = emptySet(),
    val pairedDevices: Set<BluetoothDevice> = emptySet(),
)
