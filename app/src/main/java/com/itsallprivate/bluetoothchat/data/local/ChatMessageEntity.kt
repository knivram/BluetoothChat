package com.itsallprivate.bluetoothchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceAddress: String,
    val message: String,
    val isFromLocalUser: Boolean,
    val dateTime: LocalDateTime,
)
