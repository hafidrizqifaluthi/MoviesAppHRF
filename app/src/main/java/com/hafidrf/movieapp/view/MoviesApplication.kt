package com.hafidrf.movieapp.view

import android.app.Application
import com.hafidrf.movieapp.dh.BaseDependencies
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        startKoin {
            androidContext(this@MoviesApplication)
            modules(BaseDependencies.networkingModule)
        }
    }
}