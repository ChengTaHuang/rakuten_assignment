package com.rakuten.assignment

import androidx.multidex.MultiDexApplication
import com.rakuten.assignment.service.NetworkService

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        NetworkService.init(this)
    }
}