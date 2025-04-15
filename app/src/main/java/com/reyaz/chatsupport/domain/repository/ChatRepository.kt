package com.reyaz.chatsupport.domain.repository

import com.reyaz.chatsupport.data.dto.ChatMessage
import com.reyaz.chatsupport.domain.model.ChatUpdate
import com.reyaz.chatsupport.domain.model.ChatUser
import com.reyaz.chatsupport.domain.model.UserChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatUpdates(): Flow<ChatUpdate>

    fun getChatMessagesByUserId(userId: Int): Flow<List<UserChatMessage>>

    fun sendMessage(userId: Int, chatMessage: ChatMessage)

    val chatUserList: Flow<List<ChatUser>>
}