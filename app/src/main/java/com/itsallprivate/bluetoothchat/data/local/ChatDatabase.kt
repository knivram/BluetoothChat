package com.itsallprivate.bluetoothchat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ChatDeviceEntity::class, ChatMessageEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDeviceDao(): ChatDeviceDao
    abstract fun chatMessageDao(): ChatMessageDao
}
