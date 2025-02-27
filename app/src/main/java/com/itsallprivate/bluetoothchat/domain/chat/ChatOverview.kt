package com.itsallprivate.bluetoothchat.domain.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatOverview(
    val device: BluetoothDevice,
    val latestMessage: BluetoothMessage? = null,
)
