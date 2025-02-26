package com.itsallprivate.bluetoothchat.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DiscoveryChangedReceiver(
    private val onIsDiscoveringChanged: (Boolean) -> Unit,
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                onIsDiscoveringChanged(true)
            }

            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                onIsDiscoveringChanged(false)
            }
        }
    }
}
