package com.itsallprivate.bluetoothchat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insert(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE deviceAddress = :address ORDER BY dateTime ASC")
    suspend fun getByAddress(address: String): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE deviceAddress = :address ORDER BY dateTime DESC LIMIT 1")
    suspend fun getLatestMessageForDevice(address: String): ChatMessageEntity?
}
