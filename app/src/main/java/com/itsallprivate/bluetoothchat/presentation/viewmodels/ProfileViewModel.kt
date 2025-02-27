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
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _profile = MutableStateFlow(BluetoothDevice("", "", ""))
    val profile = _profile.onStart {
        initProfile()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _profile.value)

    var name = mutableStateOf("")

    private fun initProfile() {
        viewModelScope.launch {
            val address = savedStateHandle.toRoute<Profile>().address
            _profile.update {
                chatRepository.getDevice(address).also {
                    name.value = it.name
                }
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
}
