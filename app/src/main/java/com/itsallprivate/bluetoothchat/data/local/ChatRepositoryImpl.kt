package com.itsallprivate.bluetoothchat.data.local

import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import com.itsallprivate.bluetoothchat.domain.chat.ChatOverview
import com.itsallprivate.bluetoothchat.domain.chat.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepositoryImpl(
    private val chatDeviceDao: ChatDeviceDao,
    private val chatMessageDao: ChatMessageDao,
) : ChatRepository {

    override suspend fun addMessage(device: BluetoothDevice, message: BluetoothMessage) {
        withContext(Dispatchers.IO) {
            chatDeviceDao.insertIfNotExists(
                ChatDeviceEntity(
                    address = device.address,
                    deviceName = device.deviceName,
                ),
            )
            chatMessageDao.insert(
                ChatMessageEntity(
                    deviceAddress = device.address,
                    message = message.message,
                    isFromLocalUser = message.isFromLocalUser,
                    dateTime = message.dateTime,
                ),
            )
        }
    }

    override suspend fun getMessagesForDevice(address: String): List<BluetoothMessage> {
        return withContext(Dispatchers.IO) {
            chatMessageDao.getByAddress(address).map {
                BluetoothMessage(
                    message = it.message,
                    isFromLocalUser = it.isFromLocalUser,
                    dateTime = it.dateTime,
                )
            }
        }
    }

    override suspend fun getAllDevices(): List<BluetoothDevice> {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getAll().map {
                BluetoothDevice(
                    deviceName = it.deviceName,
                    address = it.address,
                )
            }
        }
    }

    override suspend fun getChatOverviews(): List<ChatOverview> {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getAll().map { deviceEntity ->
                val latestMessageEntity = chatMessageDao.getLatestMessageForDevice(deviceEntity.address)
                val latestMessage = latestMessageEntity?.let { entity ->
                    BluetoothMessage(
                        message = entity.message,
                        isFromLocalUser = entity.isFromLocalUser,
                        dateTime = entity.dateTime,
                    )
                }
                ChatOverview(
                    name = deviceEntity.deviceName,
                    address = deviceEntity.address,
                    latestMessage = latestMessage,
                )
            }.sortedByDescending { it.latestMessage?.dateTime }
        }
    }

    override suspend fun getDevice(address: String): BluetoothDevice {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getDevice(address)?.let {
                BluetoothDevice(
                    deviceName = it.deviceName,
                    address = it.address,
                )
            } ?: throw IllegalArgumentException("Device not found")
        }
    }

    override suspend fun updateDevice(device: BluetoothDevice): BluetoothDevice {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.upsert(
                ChatDeviceEntity(
                    address = device.address,
                    deviceName = device.deviceName,
                ),
            )
            device
        }
    }
}
