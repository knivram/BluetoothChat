package com.itsallprivate.bluetoothchat.presentation.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsallprivate.bluetoothchat.Chat
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothConnectionErrorCode
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothConnectionErrorCode.CONNECTION_CLOSED
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothConnectionErrorCode.CONNECTION_FAILED
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothConnectionErrorCode.WRONG_DEVICE
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothController
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothMessage
import com.itsallprivate.bluetoothchat.domain.chat.ChatRepository
import com.itsallprivate.bluetoothchat.domain.chat.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val bluetoothController: BluetoothController,
    private val chatRepository: ChatRepository,
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

        viewModelScope.launch {
            chatRepository.getMessagesForDevice(device.address).let { loadedMessages ->
                _messages.update { loadedMessages }
            }
        }
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
                chatRepository.addMessage(device, bluetoothMessage)
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
                        showToast(WRONG_DEVICE)
                        bluetoothController.closeConnection()
                        deviceConnectionJob?.cancel()
                        _status.update { ConnectionStatus.DISCONNECTED }
                    }
                }

                is ConnectionResult.Error -> {
                    showToast(result.errorCode)
                    _status.update { ConnectionStatus.DISCONNECTED }
                }

                is ConnectionResult.MessageReceived -> {
                    _messages.update {
                        it + result.message
                    }
                    chatRepository.addMessage(device, result.message)
                }
            }
        }.catch { throwable ->
            bluetoothController.closeConnection()
            _status.update { ConnectionStatus.DISCONNECTED }
        }.launchIn(viewModelScope)
    }

    private fun showToast(errorCode: BluetoothConnectionErrorCode) {
        val message = when (errorCode) {
            CONNECTION_FAILED -> "Connection failed"
            CONNECTION_CLOSED -> "Connection closed"
            WRONG_DEVICE -> "Wrong device"
        }
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}