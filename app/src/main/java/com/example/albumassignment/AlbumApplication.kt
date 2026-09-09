package com.example.albumassignment

import android.app.Application
import com.example.albumassignment.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AlbumApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AlbumApplication)
            modules(appModule)
        }
    }
}