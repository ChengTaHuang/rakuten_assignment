package com.rakuten.assignment.activity.main

import com.rakuten.assignment.bean.ExchangeRatesResponse
import com.rakuten.assignment.service.NetworkService
import io.reactivex.Single

interface MainRepository {

    fun getExchangeRate(): Single<ExchangeRatesResponse>

    fun isNetworkConnected() : Single<Boolean>
}

class MainRepositoryImpl(private val service: NetworkService) :
    MainRepository {

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return service.api.getExchangeRates()
    }

    override fun isNetworkConnected(): Single<Boolean> {
        return Single.just(service.isConnected())
    }
}