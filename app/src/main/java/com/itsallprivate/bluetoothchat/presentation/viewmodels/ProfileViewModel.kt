package com.itsallprivate.bluetoothchat.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.itsallprivate.bluetoothchat.Profile
import com.itsallprivate.bluetoothchat.domain.chat.BluetoothDevice
import com.itsallprivate.bluetoothchat.domain.chat.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _profile = MutableStateFlow(
        savedStateHandle.toRoute<Profile>().let {
            BluetoothDevice(deviceName = it.deviceName, address = it.address)
        },
    )
    val profile = _profile.onStart {
        initProfile()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _profile.value)

    var name = mutableStateOf(_profile.value.name)

    private fun initProfile() {
        viewModelScope.launch {
            chatRepository.findDevice(_profile.value.address)?.let { loadedDevice ->
                _profile.update { loadedDevice }
                name.value = loadedDevice.name
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            val newDevice = chatRepository.updateDevice(
                profile.value.copy(name = name.value),
            )
            _profile.update { newDevice }
        }
    }

    fun reset() {
        name.value = profile.value.deviceName
    }

    fun delete() {
        viewModelScope.launch {
            chatRepository.deleteDeviceAndAllMessages(profile.value.address)
        }
    }
}
