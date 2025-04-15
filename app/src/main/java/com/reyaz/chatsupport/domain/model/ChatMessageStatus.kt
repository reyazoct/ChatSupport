package com.reyaz.chatsupport.domain.model

import com.reyaz.chatsupport.R

sealed class ChatMessageStatus {
    object Success : ChatMessageStatus()
    object Sending : ChatMessageStatus()
    object Failed : ChatMessageStatus()

    val displayMessageResId: Int
        get() = when(this) {
            Failed -> R.string.failed
            Sending -> R.string.sending
            Success -> R.string.sent
        }
}