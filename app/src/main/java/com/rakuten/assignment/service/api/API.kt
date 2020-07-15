package com.rakuten.assignment.service.api

import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Single
import retrofit2.http.*

interface API {

    @GET("latest")
    abstract fun getExchangeRates(): Single<ExchangeRatesResponse>
}