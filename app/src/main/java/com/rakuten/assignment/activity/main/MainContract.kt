package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseContract
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Single

class MainContract {
    interface Model : BaseContract.Model {
        fun getExchangeRate(): Single<ExchangeRatesResponse>

        fun convertToCountryExchangeRate(data : ExchangeRatesResponse) : Single<List<CountryExchangeRate>>

        fun getCurrentTime(): String

        fun setAmount(amount: String) : Single<List<CountryExchangeRate>>

        fun changeBaseCountry(iso : String) : Single<List<CountryExchangeRate>>

        fun isNetworkConnected() : Single<Boolean>
    }

    interface View : BaseContract.View {
        fun showExchangeRates(countryExchangeRates : List<CountryExchangeRate>)

        fun showUpdateTime(date : String)

        fun showTimeLeft(second : Int)

        fun showNetworkConnectionError()

        fun hideNetworkConnectionError()
    }

    interface Presenter {

        fun startGettingExchangeRates()

        fun stopGettingExchangeRates()

        fun setAmount(amount: String)

        fun setBaseCountry(iso: String)
    }
}