package com.reyaz.chatsupport.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    @SerialName("userId")
    val userId: Int,

    @SerialName("senderName")
    val senderName: String,

    @SerialName("message")
    val message: String,
)