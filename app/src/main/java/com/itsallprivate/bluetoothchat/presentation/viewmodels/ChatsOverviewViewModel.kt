package com.itsallprivate.bluetoothchat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsallprivate.bluetoothchat.domain.chat.ChatOverview
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
open class ChatsOverviewViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _chats = MutableStateFlow(emptyList<ChatOverview>())
    val chats = _chats
        .onStart {
            loadChats()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun loadChats() {
        viewModelScope.launch {
            chatRepository.getChatOverviews().let { loadedOverviews ->
                _chats.update { loadedOverviews }
            }
        }
    }
}
