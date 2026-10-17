package com.namabiz.agrim

import android.app.Application
import com.namabiz.agrim.core.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AgrimApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@AgrimApplication)
            modules(appModules)
        }
    }

    companion object {
        lateinit var instance: AgrimApplication
            private set
    }
}
