package com.reyaz.chatsupport.domain.model

sealed class UserReadStatus {
    object Read: UserReadStatus()
    object NotRead: UserReadStatus()
}