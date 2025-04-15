package com.reyaz.chatsupport.data.respository

import com.reyaz.chatsupport.data.socket.ChatWebSocketListener
import com.reyaz.chatsupport.domain.model.ChatPreview
import com.reyaz.chatsupport.domain.model.ChatUpdate
import com.reyaz.chatsupport.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val webSocketListener: ChatWebSocketListener,
): ChatRepository {
    override fun getChatUpdates(): Flow<ChatUpdate> {
        return webSocketListener.newChatUpdate
    }

    override fun getChatList(): List<ChatPreview> {
        return listOf(
            ChatPreview(
                userId = 1,
                userDisplayName = "Support Bot",
                lastMessage = null,
            ),
            ChatPreview(
                userId = 2,
                userDisplayName = "Sales Bot",
                lastMessage = null,
            ),
            ChatPreview(
                userId = 3,
                userDisplayName = "FAQ Bot",
                lastMessage = null,
            ),
        )
    }
}