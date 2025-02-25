package com.itsallprivate.bluetoothchat.domain.chat

sealed interface ConnectionResult {
    data class ConnectionEstablished(val device: BluetoothDevice): ConnectionResult
    data class Error(val message: String): ConnectionResult
    data class MessageReceived(val message: BluetoothMessage): ConnectionResult
}