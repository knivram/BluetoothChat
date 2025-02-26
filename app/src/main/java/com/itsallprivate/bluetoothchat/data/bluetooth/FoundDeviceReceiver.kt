package com.itsallprivate.bluetoothchat.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class FoundDeviceReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit,
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java,
                    )
                } else {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }?.let { device ->
                    val majorDeviceClass = device.bluetoothClass?.majorDeviceClass
                    if (majorDeviceClass == BluetoothClass.Device.Major.PHONE) {
                        onDeviceFound(device)
                    }
                }
            }
        }
    }
}
