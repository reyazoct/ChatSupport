package com.reyaz.chatsupport.ui.screens.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reyaz.chatsupport.base.UiData
import com.reyaz.chatsupport.data.model.ChatPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {
    private val _chatPreviewList = MutableStateFlow<UiData<List<ChatPreview>>>(UiData.Loading())
    val chatPreviewList = _chatPreviewList.asStateFlow()

    init {
        fetchChatPreviewList()
    }

    private fun fetchChatPreviewList() {
        viewModelScope.launch {
            val list = listOf(
                ChatPreview(
                    userDisplayName = "Sales BOT",
                    lastMessage = "Hello, How are you",
                ),
                ChatPreview(
                    userDisplayName = "Support BOT",
                    lastMessage = "Hello, Need Support?",
                )
            )
            _chatPreviewList.emit(UiData.Success(list))
        }
    }
}