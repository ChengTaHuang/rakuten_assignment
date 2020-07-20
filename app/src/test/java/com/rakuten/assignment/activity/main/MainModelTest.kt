package com.rakuten.assignment.activity.main

import com.rakuten.assignment.BaseTest
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import org.junit.Test
import org.mockito.Mockito
import java.math.BigDecimal

class MainModelTest : BaseTest() {

    private val repo = Mockito.mock(MainRepository::class.java)
    private val model: MainContract.Model = MainModelImpl(repo)

    @Test
    fun `test for the first time to get the initial list of country exchange rates`() {
        val rates = mapOf<String, Double>(Pair("TEST", 1.0), Pair("TEST2", 2.0))
        val base = "EUR"
        val date = "2020/07/20"
        val response = ExchangeRatesResponse(rates, base, date)

        val country1 = CountryExchangeRate(base,1.00,BigDecimal("1.0"),BigDecimal("0.0"), base = base)
        val country2 = CountryExchangeRate("TEST", 1.0, BigDecimal("1.0"), BigDecimal("0.0"), base)
        val country3 = CountryExchangeRate("TEST2", 2.0, BigDecimal("2.0"), BigDecimal("0.0"), base)

        val countryExchangeRates = listOf<CountryExchangeRate>(country1 , country2 , country3)

        model.convertToCountryExchangeRate(response).test()
            .assertValue(countryExchangeRates)
    }

    @Test
    fun `test for change base country after init exchange country rate`() {
        val rates = mapOf(Pair("TEST", 1.0), Pair("TEST2", 2.0))
        val base = "EUR"
        val date = "2020/07/20"
        val response = ExchangeRatesResponse(rates, base, date)

        val targetCountry = "TEST2"
        val newCountry1 = CountryExchangeRate("TEST2",2.0,BigDecimal("1.0"),BigDecimal("0.0"), base = targetCountry)
        val newCountry2 = CountryExchangeRate(base, 1.0, BigDecimal("0.5000"), BigDecimal("0.0"), targetCountry)
        val newCountry3 = CountryExchangeRate("TEST", 1.0, BigDecimal("0.5000"), BigDecimal("0.0"), targetCountry)

        val newCountryExchangeRates = listOf(newCountry1 , newCountry2 ,newCountry3)

        model.convertToCountryExchangeRate(response)
        model.changeBaseCountryAndResetAmount(targetCountry).test()
            .assertValue(newCountryExchangeRates)
    }

    @Test
    fun `test for change amount after init exchange country rate`() {
        val rates = mapOf(Pair("TEST", 1.0), Pair("TEST2", 2.0))
        val base = "EUR"
        val date = "2020/07/20"
        val response = ExchangeRatesResponse(rates, base, date)

        val newCountry1 = CountryExchangeRate(base,1.00,BigDecimal("1.0"),BigDecimal("10.0000"), base = base)
        val newCountry2 = CountryExchangeRate("TEST", 1.0, BigDecimal("1.0"), BigDecimal("10.0000"), base)
        val newCountry3 = CountryExchangeRate("TEST2", 2.0, BigDecimal("2.0"), BigDecimal("20.0000"), base)
        val newCountryExchangeRates = listOf(newCountry1 , newCountry2 ,newCountry3)

        model.convertToCountryExchangeRate(response)
        model.getNewCountryExchangeRateByAmount("10").test()
            .assertValue(newCountryExchangeRates)

    }
}