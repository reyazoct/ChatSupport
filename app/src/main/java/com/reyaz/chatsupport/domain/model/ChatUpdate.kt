package com.reyaz.chatsupport.domain.model

sealed class ChatUpdate {
    data class NewChatMessage(
        val userId: Int,
        val senderName: String,
        val message: String,
    ): ChatUpdate()

    data object NotConnected: ChatUpdate()
}