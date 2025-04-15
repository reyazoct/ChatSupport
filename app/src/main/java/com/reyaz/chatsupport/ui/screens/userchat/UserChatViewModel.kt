package com.reyaz.chatsupport.ui.screens.userchat

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.data.dto.ChatMessage
import com.reyaz.chatsupport.domain.model.UserChatMessage
import com.reyaz.chatsupport.domain.repository.ChatRepository
import com.reyaz.chatsupport.system.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.getValue

class UserChatViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val userId by lazy { checkNotNull(savedStateHandle.get<Int>("userId")) }
    private val repository: ChatRepository by getKoin().inject()
    private val networkMonitor: NetworkMonitor by getKoin().inject()
    val isConnected = networkMonitor.isConnected

    private val _messagesList = MutableStateFlow<UiData<List<UserChatMessage>>>(UiData.Loading())
    val messagesList = _messagesList.asStateFlow()

    private val _textFieldValue = MutableStateFlow(TextFieldValue())
    val textFieldValue = _textFieldValue.asStateFlow()

    init {
        fetchMessages()
        markAllMessagesAsRead()
    }

    private fun markAllMessagesAsRead() {
        repository.markAllAsRead(userId)
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            repository.getChatMessagesByUserId(userId).collectLatest { list ->
                if (list.isEmpty()) {
                    _messagesList.emit(UiData.Error(Exception("No messages found")))
                } else {
                    _messagesList.emit(UiData.Success(list))
                }
            }
        }
    }

    fun onTextChange(textFieldValue: TextFieldValue) {
        viewModelScope.launch {
            _textFieldValue.emit(textFieldValue)
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            val newChatMessage = ChatMessage(
                userId = 0,
                senderName = "You",
                message = _textFieldValue.value.text
            )
            repository.sendMessage(userId, newChatMessage)
            _textFieldValue.emit(TextFieldValue())
        }
    }
}