package com.rakuten.assignment

import androidx.multidex.MultiDexApplication
import com.rakuten.assignment.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(appModule)
        }
    }
}