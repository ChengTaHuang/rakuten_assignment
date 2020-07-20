package com.rakuten.assignment.activity.main

import com.rakuten.assignment.BaseTest
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class MainPresenterTest : BaseTest() {
    private val view = Mockito.mock(MainContract.View::class.java)
    private val model = Mockito.mock(MainContract.Model::class.java)
    private lateinit var presenter: MainContract.Presenter

    @Test
    fun `get exchange rate`() {
        val rates = mapOf<String , Double>(Pair("TEST",  1.0) , Pair("TEST2" , 2.0))
        val base = "EUR"
        val date = "2020/07/20"
        val response = ExchangeRatesResponse(rates , base , date)

        val country1 = CountryExchangeRate(base , 1.0 , BigDecimal(1.0) , BigDecimal(0.0) , base)
        val country2 = CountryExchangeRate("TEST" , 1.0 , BigDecimal(1.0) , BigDecimal(0.0) , base)
        val country3 = CountryExchangeRate("TEST2" , 2.0 , BigDecimal(2.0) , BigDecimal(0.0) , base)

        val countryExchangeRates = listOf<CountryExchangeRate>(country1 , country2 , country3)

        presenter = MainPresenterImpl(model, view)
        Mockito.`when`(model.getTenSecondTimer()).thenReturn(Flowable.interval(0, 10, TimeUnit.SECONDS))
        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(true))
        Mockito.`when`(model.getExchangeRate()).thenReturn(Single.just(response))
        Mockito.`when`(model.convertToCountryExchangeRate(response)).thenReturn(Single.just(countryExchangeRates))
        Mockito.`when`(model.getCurrentTime()).thenReturn(date)
        presenter.startGettingExchangeRates()
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)

        order.verify(model).getTenSecondTimer()
        order.verify(model).isNetworkConnected()
        order.verify(view).showLoading()
        order.verify(model).getExchangeRate()
        order.verify(model).convertToCountryExchangeRate(response)
        order.verify(view).hideNetworkConnectionError()
        order.verify(view).showExchangeRates(countryExchangeRates)
        order.verify(view).showUpdateTime(date)
        order.verify(view).hideLoading()

    }

    @Test
    fun `get exchange rate without internet`() {

        presenter = MainPresenterImpl(model, view)
        Mockito.`when`(model.getTenSecondTimer()).thenReturn(Flowable.interval(0, 10, TimeUnit.SECONDS))
        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(false))

        presenter.startGettingExchangeRates()
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)

        order.verify(model).getTenSecondTimer()
        order.verify(model).isNetworkConnected()
        order.verify(view).showLoading()
        order.verify(view).showNetworkConnectionError()
        order.verify(view).hideLoading()

    }

    @Test
    fun `after changing base country , get list of country exchange rate`() {
        val base = "EUR"
        val countryToBeChange = "TEST2"

        val country3 = CountryExchangeRate(countryToBeChange, 2.0 , BigDecimal(2.0) , BigDecimal(0.0) , base)
        val country1 = CountryExchangeRate(base , 1.0 , BigDecimal(1.0) , BigDecimal(0.0) , base)
        val country2 = CountryExchangeRate("TEST" , 1.0 , BigDecimal(1.0) , BigDecimal(0.0) , base)

        val countryExchangeRates = listOf<CountryExchangeRate>(country3 , country1 , country2)

        presenter = MainPresenterImpl(model, view)

        Mockito.`when`(model.changeBaseCountryAndResetAmount(countryToBeChange)).thenReturn(Single.just(countryExchangeRates))

        presenter.setBaseCountry(countryToBeChange)

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)
        order.verify(model).changeBaseCountryAndResetAmount(countryToBeChange)
        order.verify(view).showExchangeRates(countryExchangeRates)
    }

    @Test
    fun `after setting amount , get country exchange rate`() {
        val amount = "1"
        val base = "EUR"
        val country1 = CountryExchangeRate(base , 1.0 , BigDecimal(1.0) , BigDecimal(0.0) , base)
        val country2 = CountryExchangeRate("TEST" , 1.0 , BigDecimal(1.0) , BigDecimal(0.0) , base)
        val country3 = CountryExchangeRate("TEST2" , 2.0 , BigDecimal(2.0) , BigDecimal(0.0) , base)

        val countryExchangeRates = listOf<CountryExchangeRate>(country1 , country2 , country3)

        presenter = MainPresenterImpl(model, view)

        Mockito.`when`(model.getNewCountryExchangeRateByAmount(amount)).thenReturn(Single.just(countryExchangeRates))

        presenter.setAmount(amount)

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)
        order.verify(model).getNewCountryExchangeRateByAmount(amount)
        order.verify(view).showExchangeRates(countryExchangeRates)
    }

    @Test
    fun `an unexpected error occurred while trying to get the exchange rate`(){
        presenter = MainPresenterImpl(model, view)

        val throwable = Throwable()
        Mockito.`when`(model.getTenSecondTimer()).thenReturn(Flowable.interval(0, 10, TimeUnit.SECONDS))
        Mockito.`when`(model.isNetworkConnected()).thenReturn(Single.just(true))
        Mockito.`when`(model.getExchangeRate()).thenReturn(Single.error(throwable))

        presenter.startGettingExchangeRates()
        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)

        order.verify(model).getTenSecondTimer()
        order.verify(model).isNetworkConnected()
        order.verify(view).showLoading()
        order.verify(view).showError()
        order.verify(view).hideLoading()
    }

    @Test
    fun `an unexpected error occurred while changing base country`() {
        presenter = MainPresenterImpl(model, view)

        val iso = "EUR"
        val throwable = Throwable()
        Mockito.`when`(model.changeBaseCountryAndResetAmount(iso)).thenReturn(Single.error(throwable))

        presenter.setBaseCountry(iso)

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)
        order.verify(model).changeBaseCountryAndResetAmount(iso)
        order.verify(view).showError()
    }

    @Test
    fun `an unexpected error occurred while trying to set amount`() {
        presenter = MainPresenterImpl(model, view)

        val amount = "1"
        val throwable = Throwable()
        Mockito.`when`(model.getNewCountryExchangeRateByAmount(amount)).thenReturn(Single.error(throwable))

        presenter.setAmount(amount)

        rxjavaTestRule.scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        val order = Mockito.inOrder(view, model)
        order.verify(model).getNewCountryExchangeRateByAmount(amount)
        order.verify(view).showError()
    }
}