package com.tukangencrypt.stegasaurus

import android.app.Application
import com.tukangencrypt.stegasaurus.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@MainApp) }
    }
}