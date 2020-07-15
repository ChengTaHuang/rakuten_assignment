package com.rakuten.assignment.activity.main

import com.rakuten.assignment.bean.ExchangeRatesResponse
import com.rakuten.assignment.service.NetworkService
import io.reactivex.Single

interface MainRepository {

    fun getExchangeRate(): Single<ExchangeRatesResponse>
}

class MainRepositoryImpl(private val service: NetworkService) :
    MainRepository {

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return service.api.getExchangeRates()
    }
}