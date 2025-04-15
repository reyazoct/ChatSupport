package com.reyaz.chatsupport.domain.model

sealed interface ChatMessageOwner {
    data object You: ChatMessageOwner
    data object Other: ChatMessageOwner
}