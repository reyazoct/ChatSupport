package com.reyaz.chatsupport.ui.screens.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.domain.model.ChatPreview
import com.reyaz.chatsupport.domain.model.ChatUpdate
import com.reyaz.chatsupport.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class ChatListViewModel : ViewModel() {
    private val repository: ChatRepository by getKoin().inject()

    private val _chatPreviewList = MutableStateFlow<UiData<List<ChatPreview>>>(UiData.Loading())
    val chatPreviewList = _chatPreviewList.asStateFlow()

    init {
        fetchChatPreviewList()
        createChatPreviewList()
    }

    private fun createChatPreviewList() {
        viewModelScope.launch {
            val chatPreviewList = repository.getChatList()
            _chatPreviewList.emit(UiData.Success(chatPreviewList))
        }
    }

    private fun fetchChatPreviewList() {
        viewModelScope.launch {
            repository.getChatUpdates().collect { chatUpdate ->
                when (chatUpdate) {
                    is ChatUpdate.NewChatMessage -> {
                        updateNewChatMessage(chatUpdate)
                    }

                    ChatUpdate.NotConnected -> {}
                }
            }
        }
    }

    private suspend fun updateNewChatMessage(chatUpdate: ChatUpdate.NewChatMessage) {
        val chatPreviewList = _chatPreviewList.value.dataOrNull?.toMutableList() ?: mutableListOf()
        chatPreviewList.removeIf { it.userId == chatUpdate.userId }
        val chatPreview = ChatPreview(
            userId = chatUpdate.userId,
            userDisplayName = chatUpdate.senderName,
            lastMessage = chatUpdate.message,
        )
        chatPreviewList.add(0, chatPreview)
        _chatPreviewList.emit(UiData.Success(chatPreviewList))
    }
}