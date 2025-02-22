package com.example.bluetooth_chat.data.chat

import com.example.bluetooth_chat.domain.chat.BluetoothMessage

// TODO: Use json serialization instead of string manipulation

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    val (senderName, message) = this.split("#")
    return BluetoothMessage(
        message = message,
        senderName = senderName,
        isFromLocalUser = isFromLocalUser
    )
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return "$senderName#$message".encodeToByteArray()
}