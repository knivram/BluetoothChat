package com.itsallprivate.bluetoothchat.domain.chat

interface ChatRepository {
    suspend fun addMessage(device: BluetoothDevice, message: BluetoothMessage)
    suspend fun getMessagesForDevice(address: String): List<BluetoothMessage>
    suspend fun getAllDevices(): List<BluetoothDevice>
    suspend fun getChatOverviews(): List<ChatOverview>
    suspend fun findDevice(address: String): BluetoothDevice?
    suspend fun updateDevice(device: BluetoothDevice): BluetoothDevice
    suspend fun deleteDeviceAndAllMessages(address: String)
}
