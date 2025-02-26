package com.itsallprivate.bluetoothchat.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothConnectionErrorCode
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothController
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import com.itsallprivate.bluetoothchat.domain.chat.ConnectionClosedException
import com.itsallprivate.bluetoothchat.domain.chat.ConnectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.time.LocalDateTime
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context,
) : BluetoothController {

    private val bluetoothManager: BluetoothManager? by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private var dataTransferService: BluetoothDataTransferService? = null

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    override val isDiscovering: StateFlow<Boolean>
        get() = _isDiscovering.asStateFlow()

    private val _scannedDevices = MutableStateFlow<Set<BluetoothDeviceDomain>>(emptySet())
    override val scannedDevices: StateFlow<Set<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<Set<BluetoothDeviceDomain>>(emptySet())
    override val pairedDevices: StateFlow<Set<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            devices + device.toBluetoothDeviceDomain()
        }
    }

    private val discoveryChangedReceiver = DiscoveryChangedReceiver { isDiscovering ->
        _isDiscovering.update { isDiscovering }
    }

    // Flag to track receivers registration status
    private var isFoundDeviceReceiverRegistered = false
    private var isDiscoveryChangedReceiverRegistered = false

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        context.registerReceiver(
            discoveryChangedReceiver,
            IntentFilter()
                .apply {
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                },
        )
        isDiscoveryChangedReceiverRegistered = true

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND),
        )
        isFoundDeviceReceiverRegistered = true

        startDiscovery()
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        updatePairedDevices()

        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            stopDiscovery()

            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "chat_service",
                UUID.fromString(SERVICE_UUID),
            )

            var shouldLoop = true
            while (shouldLoop) {
                try {
                    currentClientSocket = currentServerSocket?.accept()?.also {
                        emit(ConnectionResult.ConnectionEstablished(it.remoteDevice.toBluetoothDeviceDomain()))
                    }
                } catch (e: IOException) {
                    shouldLoop = false
                }
                currentClientSocket?.let { clientSocket ->
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(clientSocket)
                    dataTransferService = service

                    try {
                        emitAll(
                            service
                                .listenForIncomingMessages()
                                .map {
                                    ConnectionResult.MessageReceived(it)
                                },
                        )
                    } catch (e: ConnectionClosedException) {
                        clientSocket.close()
                        currentClientSocket = null
                        emit(ConnectionResult.Error(BluetoothConnectionErrorCode.CONNECTION_CLOSED))
                    } catch (e: IOException) {
                        clientSocket.close()
                        currentClientSocket = null
                        emit(ConnectionResult.Error(BluetoothConnectionErrorCode.CONNECTION_FAILED))
                    }
                }
            }
        }.onCompletion {
            closeServerSocket()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            stopDiscovery()

            currentClientSocket = bluetoothAdapter
                ?.getRemoteDevice(device.address)
                ?.createRfcommSocketToServiceRecord(
                    UUID.fromString(SERVICE_UUID),
                )

            currentClientSocket?.let { socket ->
                try {
                    currentServerSocket?.close()
                    socket.connect()
                    emit(ConnectionResult.ConnectionEstablished(socket.remoteDevice.toBluetoothDeviceDomain()))
                    BluetoothDataTransferService(socket).also { service ->
                        dataTransferService = service
                        emitAll(
                            service.listenForIncomingMessages()
                                .map { message ->
                                    ConnectionResult.MessageReceived(message)
                                },
                        )
                    }
                } catch (e: ConnectionClosedException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error(BluetoothConnectionErrorCode.CONNECTION_CLOSED))
                } catch (e: IOException) {
                    socket.close()
                    currentClientSocket = null
                    emit(ConnectionResult.Error(BluetoothConnectionErrorCode.CONNECTION_FAILED))
                }
            }
        }.onCompletion {
            closeClientSocket()
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendMessage(message: String): BluetoothMessage? {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return null
        }

        return dataTransferService?.let { service ->
            val bluetoothMessage = BluetoothMessage(
                message = message,
                isFromLocalUser = true,
                dateTime = LocalDateTime.now(),
            )
            val successful = service.sendMessage(bluetoothMessage.toByteArray())
            if (successful) {
                bluetoothMessage
            } else {
                null
            }
        }
    }

    private fun closeClientSocket() {
        currentClientSocket?.close()
        currentClientSocket = null
    }

    private fun closeServerSocket() {
        currentServerSocket?.close()
        currentServerSocket = null
    }

    override fun closeAllConnections() {
        closeClientSocket()
        closeServerSocket()
    }

    override fun release() {
        if (isFoundDeviceReceiverRegistered) {
            context.unregisterReceiver(foundDeviceReceiver)
            isFoundDeviceReceiverRegistered = false
        }
        if (isDiscoveryChangedReceiverRegistered) {
            context.unregisterReceiver(discoveryChangedReceiver)
            isDiscoveryChangedReceiverRegistered = false
        }
        closeAllConnections()
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.mapTo(mutableSetOf()) { it.toBluetoothDeviceDomain() }
            ?.let { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val SERVICE_UUID = "27b7d1da-08c7-4505-a6d1-2459987e5e2d"
    }
}
