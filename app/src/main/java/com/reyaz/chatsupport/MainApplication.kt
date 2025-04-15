package com.reyaz.chatsupport

import android.app.Application
import com.reyaz.chatsupport.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(allModules())
        }
    }
}