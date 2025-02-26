package com.itsallprivate.bluetoothchat.data.bluetooth

import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
    return Json.decodeFromString<BluetoothMessage>(this).copy(
        isFromLocalUser = isFromLocalUser
    )
}

fun BluetoothMessage.toByteArray(): ByteArray {
    return Json.encodeToString(this).toByteArray()
}