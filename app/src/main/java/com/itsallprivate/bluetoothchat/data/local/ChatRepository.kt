package com.itsallprivate.bluetoothchat.data.local

import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository(
    private val chatDeviceDao: ChatDeviceDao,
    private val chatMessageDao: ChatMessageDao
) {
    suspend fun addMessage(device: BluetoothDeviceDomain, message: BluetoothMessage) {
        withContext(Dispatchers.IO) {
            chatDeviceDao.insertIfNotExists(
                ChatDeviceEntity(
                    address = device.address,
                    name = device.name
                )
            )
            chatMessageDao.insert(
                ChatMessageEntity(
                    deviceAddress = device.address,
                    message = message.message,
                    isFromLocalUser = message.isFromLocalUser,
                    dateTime = message.dateTime,
                )
            )
        }
    }

    suspend fun getMessagesForDevice(address: String): List<BluetoothMessage> {
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

    suspend fun getAllDevices(): List<BluetoothDeviceDomain> {
        return withContext(Dispatchers.IO) {
            chatDeviceDao.getAll().map {
                BluetoothDeviceDomain(
                    name = it.name,
                    address = it.address
                )
            }
        }
    }
}
