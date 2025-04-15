package com.reyaz.chatsupport.domain.model

data class UserChatMessage(
    val message: String,
    val owner: ChatMessageOwner,
    val status: ChatMessageStatus,
    val userReadStatus: UserReadStatus = UserReadStatus.NotRead,
)
