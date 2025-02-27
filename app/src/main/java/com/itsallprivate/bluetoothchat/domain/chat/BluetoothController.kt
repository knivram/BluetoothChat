package com.itsallprivate.bluetoothchat.domain.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isDiscovering: StateFlow<Boolean>
    val scannedDevices: StateFlow<Set<BluetoothDevice>>
    val pairedDevices: StateFlow<Set<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>

    suspend fun trySendMessage(message: String): BluetoothMessage?

    fun closeAllConnections()
    fun release()
}
