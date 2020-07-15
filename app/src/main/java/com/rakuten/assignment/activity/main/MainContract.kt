package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseContract
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Single

class MainContract {
    interface Model : BaseContract.Model {
        fun getExchangeRate(): Single<ExchangeRatesResponse>
    }

    interface View : BaseContract.View {
        fun showExchangeRates(rates: Map<String, Double>)

        fun showUpdateTime(date : String)
    }

    interface Presenter {

        fun startGettingExchangeRates()

        fun stopGettingExchangeRates()
    }
}