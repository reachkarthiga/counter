package com.personal_app.counter

import android.app.Application
import com.personal_app.counter.counterTag.CounterViewModel
import com.personal_app.counter.dataBase.Database
import com.personal_app.counter.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App:Application() {

    override fun onCreate() {
        super.onCreate()

        val module = module {
            single { Database.getInstance(this@App) }
            single { CounterViewModel(get()) }
            single { SettingsViewModel(get()) }
        }

        startKoin {
            androidContext(this@App)
            modules(module)
        }

    }


}