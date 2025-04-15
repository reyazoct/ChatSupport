package com.reyaz.chatsupport.di

import com.reyaz.chatsupport.data.respository.ChatRepositoryImpl
import com.reyaz.chatsupport.data.socket.ChatWebSocketListener
import com.reyaz.chatsupport.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.qualifier.named
import org.koin.dsl.module

private fun repositoryModule() = module {
    single<ChatRepository> { ChatRepositoryImpl(get(), get(named("globalScope"))) }
}

private fun miscModule() = module {
    single<ChatWebSocketListener> {
        val client = OkHttpClient()
        val request = Request.Builder().url("wss://s14461.blr1.piesocket.com/v3/1?api_key=9JQ2KpVXS5bZfu6clKmMBbrqEXZq1EPUhphnfvvi").build()
        val listener = ChatWebSocketListener(get(named("globalScope")))
        client.newWebSocket(request, listener)
//        client.dispatcher.executorService.shutdown()
        listener
    }
    single<CoroutineScope>(named("globalScope")) {
        CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
}

fun allModules() = listOf(
    repositoryModule(),
    miscModule()
)