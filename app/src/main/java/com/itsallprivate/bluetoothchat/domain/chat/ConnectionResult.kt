package com.itsallprivate.bluetoothchat.domain.chat

sealed interface ConnectionResult {
    data object ConnectionEstablished: ConnectionResult
    data class Error(val message: String): ConnectionResult
    data class MessageReceived(val message: BluetoothMessage): ConnectionResult
}