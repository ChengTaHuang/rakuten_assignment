package com.rakuten.assignment.service

import android.app.Application
import com.google.gson.GsonBuilder
import com.rakuten.assignment.R
import com.rakuten.assignment.service.api.API
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkService {
    lateinit var api: API
    fun init(application: Application) {
        api = configRetrofit(API::class.java, application.getString(R.string.domain))
    }

    private fun <T> configRetrofit(service: Class<T>, domain: String): T {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(domain)
            .client(configClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(service)
    }

    private fun configClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.connectTimeout(5, TimeUnit.SECONDS)
        okHttpClient.readTimeout(5, TimeUnit.SECONDS)
        return okHttpClient.build()
    }
}