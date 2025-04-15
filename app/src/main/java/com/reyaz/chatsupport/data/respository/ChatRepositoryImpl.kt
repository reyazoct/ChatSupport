package com.reyaz.chatsupport.data.respository

import com.reyaz.chatsupport.data.socket.ChatWebSocketListener
import com.reyaz.chatsupport.domain.model.ChatUpdate
import com.reyaz.chatsupport.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    private val webSocketListener: ChatWebSocketListener,
): ChatRepository {
    override fun getChatUpdates(): Flow<ChatUpdate> {
        return webSocketListener.newChatUpdate
    }
}