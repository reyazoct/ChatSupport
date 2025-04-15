package com.reyaz.chatsupport.domain.repository

import com.reyaz.chatsupport.domain.model.ChatPreview
import com.reyaz.chatsupport.domain.model.ChatUpdate
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatUpdates(): Flow<ChatUpdate>

    fun getChatList(): List<ChatPreview>
}