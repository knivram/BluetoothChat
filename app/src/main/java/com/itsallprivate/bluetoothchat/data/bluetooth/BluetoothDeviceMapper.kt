package com.itsallprivate.bluetoothchat.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        deviceName = name ?: "(Unknown)",
        address = address,
    )
}
