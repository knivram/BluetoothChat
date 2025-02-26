package com.itsallprivate.bluetoothchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_devices")
data class ChatDeviceEntity(
    @PrimaryKey val address: String,
    val name: String,
)
