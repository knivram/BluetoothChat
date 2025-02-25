package com.itsallprivate.bluetoothchat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDeviceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(device: ChatDeviceEntity)

    @Query("SELECT * FROM chat_devices WHERE address = :address")
    suspend fun getDevice(address: String): ChatDeviceEntity?

    @Query("SELECT * FROM chat_devices")
    suspend fun getAllDevices(): List<ChatDeviceEntity>
}
