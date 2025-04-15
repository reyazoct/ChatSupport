package com.reyaz.chatsupport.ui.screens.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.domain.model.ChatPreview
import com.reyaz.chatsupport.domain.model.UserReadStatus
import com.reyaz.chatsupport.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin

class ChatListViewModel : ViewModel() {
    private val repository: ChatRepository by getKoin().inject()

    private val _chatPreviewList = MutableStateFlow<UiData<List<ChatPreview>>>(UiData.Loading())
    val chatPreviewList = _chatPreviewList.asStateFlow()

    init {
        fetchChatPreviewList()
    }

    private fun fetchChatPreviewList() {
        viewModelScope.launch {
            repository.chatUserList.collectLatest { chatUserList ->
                val chatPreview = chatUserList.map { chatUser ->
                    val unreadMessagesCount = chatUser.messages.count { it.userReadStatus == UserReadStatus.NotRead }
                    ChatPreview(
                        userId = chatUser.userId,
                        userDisplayName = chatUser.senderName,
                        lastMessage = chatUser.messages.firstOrNull()?.message,
                        unreadMessagesCount = if (unreadMessagesCount == 0) null else unreadMessagesCount,
                    )
                }
                _chatPreviewList.emit(UiData.Success(chatPreview))
            }
        }
    }
}