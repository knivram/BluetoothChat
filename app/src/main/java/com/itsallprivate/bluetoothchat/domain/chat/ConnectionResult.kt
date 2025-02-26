package com.itsallprivate.bluetoothchat.domain.chat

enum class BluetoothConnectionErrorCode {
    WRONG_DEVICE,
    CONNECTION_FAILED,
    CONNECTION_CLOSED,
}

sealed interface ConnectionResult {
    data class ConnectionEstablished(val device: BluetoothDevice) : ConnectionResult
    data class Error(val errorCode: BluetoothConnectionErrorCode) : ConnectionResult
    data class MessageReceived(val message: BluetoothMessage) : ConnectionResult
}
