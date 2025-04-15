package com.reyaz.chatsupport.domain.model

data class ChatUser(
    val userId: Int,
    val senderName: String,
    val messages: List<UserChatMessage>,
)