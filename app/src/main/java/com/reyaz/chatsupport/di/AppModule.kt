package com.reyaz.chatsupport.di

import com.reyaz.chatsupport.data.respository.ChatRepositoryImpl
import com.reyaz.chatsupport.data.socket.ChatWebSocketListener
import com.reyaz.chatsupport.domain.repository.ChatRepository
import com.reyaz.chatsupport.system.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private fun repositoryModule() = module {
    single<ChatRepository> {
        ChatRepositoryImpl(
            get(),
            get(named("globalScope")),
            get(),
        )
    }
}

private fun miscModule() = module {
    single<ChatWebSocketListener> {
        ChatWebSocketListener(get(named("globalScope")))
    }
    single<CoroutineScope>(named("globalScope")) {
        CoroutineScope(Dispatchers.Main + SupervisorJob()   )
    }
    single<NetworkMonitor> {
        NetworkMonitor(
            androidContext(),
            get(named("globalScope"))
        )
    }
}

fun allModules() = listOf(
    repositoryModule(),
    miscModule()
)