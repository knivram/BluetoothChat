package com.itsallprivate.bluetoothchat.domain.chat

import kotlinx.serialization.Serializable

typealias BluetoothDeviceDomain = BluetoothDevice

@Serializable
data class BluetoothDevice(
    val deviceName: String,
    val address: String,
    val name: String = deviceName,
)
