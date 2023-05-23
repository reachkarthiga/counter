package com.example.counter

import android.app.Application
import com.example.counter.counterTag.CounterViewModel
import com.example.counter.dataBase.Database
import com.example.counter.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
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