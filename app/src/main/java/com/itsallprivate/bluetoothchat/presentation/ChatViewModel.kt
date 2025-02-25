package com.itsallprivate.bluetoothchat.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsallprivate.bluetoothchat.Chat
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothController
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import com.itsallprivate.bluetoothchat.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ConnectionStatus {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bluetoothController: BluetoothController
) : ViewModel() {
    private val _messages = MutableStateFlow(emptyList<BluetoothMessage>())
    val messages = _messages.asStateFlow()

    private val _status = MutableStateFlow(ConnectionStatus.CONNECTING)
    val status = _status.asStateFlow()

    val device by lazy {
        try {
            savedStateHandle.toRoute<Chat>().let {
                BluetoothDevice(name = it.name, address = it.address)
            }
        } catch (e: Exception) {
            BluetoothDevice("Not Found", "")
        }
    }

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _status.update {
                if (isConnected) {
                    ConnectionStatus.CONNECTED
                } else {
                    ConnectionStatus.DISCONNECTED
                }
            }
        }.launchIn(viewModelScope)
    }

    fun disconnectFromDevice() {
        bluetoothController.closeConnection()
        deviceConnectionJob?.cancel()
        _status.update { ConnectionStatus.DISCONNECTED }
    }

    fun connectConnectToDevice() {
        _status.update { ConnectionStatus.CONNECTING }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    fun waitForIncomingConnections() {
        _status.update { ConnectionStatus.CONNECTING }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            bluetoothController.trySendMessage(message)?.let { bluetoothMessage ->
                _messages.update {
                    it + bluetoothMessage
                }
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    if (result.device.address == device.address) {
                        _status.update { ConnectionStatus.CONNECTED }
                    } else {
                        bluetoothController.closeConnection()
                        deviceConnectionJob?.cancel()
                        _status.update { ConnectionStatus.DISCONNECTED }
                    }
                }

                is ConnectionResult.Error -> {
                    _status.update { ConnectionStatus.DISCONNECTED }
                }

                is ConnectionResult.MessageReceived -> {
                    _messages.update {
                        it + result.message
                    }
                }
            }
        }
            .catch { throwable ->
                bluetoothController.closeConnection()
                _status.update { ConnectionStatus.DISCONNECTED }
            }
            .launchIn(viewModelScope)
    }
}