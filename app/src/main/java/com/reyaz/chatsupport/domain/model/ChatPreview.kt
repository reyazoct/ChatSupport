package com.reyaz.chatsupport.domain.model

data class ChatPreview(
    val userId: Int,
    val userDisplayName: String,
    val lastMessage: String?,
    val unreadMessagesCount: Int?,
)
