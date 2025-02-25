package com.itsallprivate.bluetoothchat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsallprivate.bluetoothchat.data.local.ChatRepository
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ChatsOverviewViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _chats = MutableStateFlow(emptyList<BluetoothDevice>())
    val chats = _chats.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.getAllDevices().let { loadedDevices ->
                _chats.update { loadedDevices }
            }
        }
    }
}