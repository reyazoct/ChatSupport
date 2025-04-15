package com.reyaz.chatsupport.util

inline fun <T> MutableList<T>.replaceFirst(newItem: T?, predicate: (T) -> Boolean) {
    if (newItem == null) return
    val index = indexOfFirst(predicate)
    if (index != -1) this[index] = newItem
}