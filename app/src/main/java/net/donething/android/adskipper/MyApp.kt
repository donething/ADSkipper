package net.donething.android.adskipper

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        lateinit var app: MyApp
            private set
    }
}