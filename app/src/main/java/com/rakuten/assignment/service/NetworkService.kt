package com.rakuten.assignment.service

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.gson.GsonBuilder
import com.rakuten.assignment.R
import com.rakuten.assignment.service.api.API
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkService(application: Application) {
    val api: API
    private val connectivityManager: ConnectivityManager

    init {
        api = configRetrofit(API::class.java, application.getString(R.string.domain))
        connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    }

    fun isConnected(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
            else -> {
                // Use depreciated methods only on older devices
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                nwInfo.isConnected
            }
        }
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