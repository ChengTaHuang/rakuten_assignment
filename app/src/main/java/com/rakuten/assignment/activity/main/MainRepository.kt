package com.rakuten.assignment.activity.main

import com.rakuten.assignment.bean.ExchangeRatesResponse
import com.rakuten.assignment.service.NetworkService
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

interface MainRepository {

    fun getExchangeRate(): Single<ExchangeRatesResponse>

    fun isNetworkConnected() : Single<Boolean>

    fun getTenSecondTimer(): Flowable<Long>
}

class MainRepositoryImpl(private val service: NetworkService) :
    MainRepository {

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return service.api.getExchangeRates()
    }

    override fun isNetworkConnected(): Single<Boolean> {
        return Single.just(service.isConnected())
    }

    override fun getTenSecondTimer(): Flowable<Long> {
        return Flowable.interval(0, 10, TimeUnit.SECONDS).onBackpressureDrop()
    }
}