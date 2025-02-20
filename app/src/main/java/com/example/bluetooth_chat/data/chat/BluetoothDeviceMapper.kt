package com.example.bluetooth_chat.data.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.bluetooth_chat.domain.chat.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address,
    )
}