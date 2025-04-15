package com.reyaz.chatsupport.base

sealed class UiData<T> {
    data class Success<T>(val data: T) : UiData<T>()
    data class Error<T>(val throwable: Throwable) : UiData<T>()
    class Loading<T> : UiData<T>()
}