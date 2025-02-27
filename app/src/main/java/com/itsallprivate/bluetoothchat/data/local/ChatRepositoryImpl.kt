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
                device.toEntity(),
            )
            chatMessageDao.insert(
                message.toEntity(device.address),
            )
        }
    }

    override suspend fun getMessagesForDevice(address: String): List<BluetoothMessage> {
        return withContext(Dispatchers.IO) {
            chatMessageDao.getByAddress(address).map {
                it.toDomain()
            }
        }
    }

    override suspend fun getAllDevices(): List<BluetoothDevice> {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getAll().map {
                it.toDomain()
            }
        }
    }

    override suspend fun getChatOverviews(): List<ChatOverview> {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getAll().map { deviceEntity ->
                val latestMessageEntity = chatMessageDao.getLatestMessageForDevice(deviceEntity.address)
                val latestMessage = latestMessageEntity?.toDomain()
                ChatOverview(
                    device = deviceEntity.toDomain(),
                    latestMessage = latestMessage,
                )
            }.sortedByDescending { it.latestMessage?.dateTime }
        }
    }

    override suspend fun findDevice(address: String): BluetoothDevice? {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getDevice(address)?.toDomain()
        }
    }

    override suspend fun updateDevice(device: BluetoothDevice): BluetoothDevice {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.upsert(
                device.toEntity(),
            )
            device
        }
    }

    override suspend fun deleteDeviceAndAllMessages(address: String) {
        withContext(Dispatchers.IO) {
            chatMessageDao.deleteByAddress(address)
            chatDeviceDao.deleteByAddress(address)
        }
    }
}

private fun BluetoothMessage.toEntity(deviceAddress: String) = ChatMessageEntity(
    message = message,
    isFromLocalUser = isFromLocalUser,
    dateTime = dateTime,
    deviceAddress = deviceAddress,
)

private fun ChatMessageEntity.toDomain() = BluetoothMessage(
    message = message,
    isFromLocalUser = isFromLocalUser,
    dateTime = dateTime,
)

private fun ChatDeviceEntity.toDomain() = BluetoothDevice(
    deviceName = deviceName,
    address = address,
    name = name,
)

private fun BluetoothDevice.toEntity() = ChatDeviceEntity(
    deviceName = deviceName,
    address = address,
    name = name,
)
