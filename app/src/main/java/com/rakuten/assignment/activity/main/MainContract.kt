package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseContract
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Flowable
import io.reactivex.Single

class MainContract {
    interface Model : BaseContract.Model {
        fun getExchangeRate(): Single<ExchangeRatesResponse>

        fun convertToCountryExchangeRate(data : ExchangeRatesResponse) : Single<List<CountryExchangeRate>>

        fun getCurrentTime(): String

        fun getNewCountryExchangeRateByAmount(amount: String) : Single<List<CountryExchangeRate>>

        fun changeBaseCountryAndResetAmount(iso : String) : Single<List<CountryExchangeRate>>

        fun isNetworkConnected() : Single<Boolean>

        fun getTenSecondTimer() : Flowable<Long>
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