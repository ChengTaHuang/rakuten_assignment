package com.rakuten.assignment.activity.splash

import com.rakuten.assignment.bean.ExchangeRatesResponse
import com.rakuten.assignment.service.NetworkService
import io.reactivex.Single

interface SplashRepository {

    fun getExchangeRate(): Single<ExchangeRatesResponse>
}

class SplashRepositoryImpl(private val service: NetworkService) :
    SplashRepository {

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return service.api.getExchangeRates()
    }
}