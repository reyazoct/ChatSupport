package com.reyaz.chatsupport.data.respository

import com.reyaz.chatsupport.data.socket.ChatWebSocketListener
import com.reyaz.chatsupport.domain.model.ChatMessageOwner
import com.reyaz.chatsupport.domain.model.ChatUpdate
import com.reyaz.chatsupport.domain.model.ChatUser
import com.reyaz.chatsupport.domain.model.UserChatMessage
import com.reyaz.chatsupport.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChatRepositoryImpl(
    private val webSocketListener: ChatWebSocketListener,
    private val globalCoroutineScope: CoroutineScope,
) : ChatRepository {

    private val _chatUserList = MutableStateFlow(createChatUserList())
    override val chatUserList: Flow<List<ChatUser>>
        get() = _chatUserList.asStateFlow()

    init {
        setupCollectors()
    }

    private fun setupCollectors() {
        globalCoroutineScope.launch {
            webSocketListener.newChatUpdate.collectLatest {
                when (it) {
                    is ChatUpdate.NewChatMessage -> {
                        updateNewChatMessage(it)
                    }

                    ChatUpdate.NotConnected -> {

                    }
                }
            }
        }
    }

    private suspend fun updateNewChatMessage(message: ChatUpdate.NewChatMessage) {
        val oldList = _chatUserList.value.toMutableList()
        val chatUser = oldList.firstOrNull {
            message.userId == it.userId
        }
        if (chatUser == null) {
            oldList.add(
                ChatUser(
                    userId = message.userId,
                    senderName = message.senderName,
                    messages = listOf(UserChatMessage(message.message, ChatMessageOwner.Other))
                )
            )
        } else {
            val messagesList = chatUser.messages.toMutableList()
            messagesList.add(0, UserChatMessage(message.message, ChatMessageOwner.Other))
            val newChatUser = chatUser.copy(messages = messagesList)
            oldList.remove(chatUser)
            oldList.add(0, newChatUser)
        }
        _chatUserList.emit(oldList)
    }

    override fun getChatUpdates(): Flow<ChatUpdate> {
        return webSocketListener.newChatUpdate
    }

    override fun getChatMessagesByUserId(userId: Int): Flow<List<UserChatMessage>> {
        return _chatUserList.map { chatUser ->
            chatUser.firstOrNull { it.userId == userId }?.messages ?: emptyList()
        }
    }

    private fun createChatUserList(): List<ChatUser> {
        return listOf(
            ChatUser(
                userId = 1,
                senderName = "Support Bot",
                messages = emptyList(),
            ),
            ChatUser(
                userId = 2,
                senderName = "Sales Bot",
                messages = emptyList(),
            ),
            ChatUser(
                userId = 3,
                senderName = "FAQ Bot",
                messages = emptyList(),
            ),
        )

    }
}