package com.itsallprivate.bluetoothchat.domain.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatOverview(
    val name: String?,
    val address: String,
    val latestMessage: BluetoothMessage? = null,
)
