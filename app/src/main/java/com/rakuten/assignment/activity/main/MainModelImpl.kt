package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseModelImpl
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Single

class MainModelImpl(private val repo: MainRepository) : BaseModelImpl(),
    MainContract.Model {

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return repo.getExchangeRate()
    }
}