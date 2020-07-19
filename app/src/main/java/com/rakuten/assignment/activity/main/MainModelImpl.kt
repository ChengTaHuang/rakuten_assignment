package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseModelImpl
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import com.rakuten.assignment.utils.TimeFormat
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class MainModelImpl(private val repo: MainRepository) : BaseModelImpl(),
    MainContract.Model {
    private var countryExchangeRates = mutableListOf<CountryExchangeRate>()
    private var currentInputAmount: String = "0"

    override fun isNetworkConnected(): Single<Boolean> {
        return repo.isNetworkConnected()
    }

    override fun getTenSecondTimer(): Flowable<Long> {
        return repo.getTenSecondTimer()
    }

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return repo.getExchangeRate()
    }


    //if countryExchangeRates is empty , give it new data , else calculate amount with new rate
    override fun convertToCountryExchangeRate(data: ExchangeRatesResponse): Single<List<CountryExchangeRate>> {
        if (countryExchangeRates.isEmpty()) {
            countryExchangeRates.addAll(initCountryExchangeRate(data.rates, data.base))
        } else {
            countryExchangeRates = updateExchangeRate(data.rates, currentInputAmount)
        }
        return Single.just(countryExchangeRates)
    }

    //convert to dd-MM-yyyy HH:mm:ss
    override fun getCurrentTime(): String {
        return TimeFormat.time(Date(System.currentTimeMillis()))
    }

    //replace the base country and calculate new exchange rate
    override fun changeBaseCountryAndResetAmount(iso: String): Single<List<CountryExchangeRate>> {
        val newData = mutableListOf<CountryExchangeRate>()
        val baseCountry = countryExchangeRates.find { it.iso == iso }!!
        //put target country first in the list
        newData.add(baseCountry.copy(base = iso, rate = BigDecimal(1.0.toString())))

        countryExchangeRates
            .filter { it.iso != baseCountry.iso }
            .forEach { country ->
                val newRate = convertRate(country.eurExchangeRate, baseCountry.eurExchangeRate)
                newData.add(
                    CountryExchangeRate(
                        country.iso,
                        country.eurExchangeRate,
                        newRate,
                        BigDecimal(0.0.toString()),
                        baseCountry.iso
                    )
                )
            }
        countryExchangeRates = newData
        return Single.just(countryExchangeRates)
    }

    override fun getNewCountryExchangeRateByAmount(amount: String): Single<List<CountryExchangeRate>> {
        this.currentInputAmount = amount
        val newData = mutableListOf<CountryExchangeRate>()
        countryExchangeRates.forEach {
            newData.add(
                CountryExchangeRate(
                    it.iso,
                    it.eurExchangeRate,
                    it.rate,
                    convertCurrency(amount, it.rate),
                    it.base
                )
            )
        }
        countryExchangeRates = newData
        return Single.just(countryExchangeRates)
    }

    private fun initCountryExchangeRate(rates: Map<String, Double>, base: String): MutableList<CountryExchangeRate> {
        val countryExchangeRates = mutableListOf<CountryExchangeRate>()
        //first country is base country , it's not in the rates(Map<String , Double>)
        countryExchangeRates.add(
            CountryExchangeRate(
                iso = base,
                eurExchangeRate = 1.00,
                rate = BigDecimal(1.0.toString()),
                amount = BigDecimal(0.0.toString()),
                base = base
            )
        )
        for ((iso, rate) in rates) {
            countryExchangeRates.add(
                CountryExchangeRate(
                    iso = iso,
                    eurExchangeRate = rate,
                    rate = BigDecimal(rate.toString()),
                    amount = BigDecimal(0.0.toString()),
                    base = base
                )
            )
        }
        return countryExchangeRates
    }

    private fun updateExchangeRate(rates: Map<String, Double>, amount: String): MutableList<CountryExchangeRate> {
        val newData = mutableListOf<CountryExchangeRate>()
        val baseCountry = countryExchangeRates[0]
        countryExchangeRates.forEach {
            val currentEurExchangeRate = if (rates[it.iso] == null) 1.0 else rates[it.iso]!!

            val newRate = convertRate(currentEurExchangeRate, baseCountry.eurExchangeRate)

            newData.add(
                CountryExchangeRate(
                    it.iso,
                    currentEurExchangeRate,
                    newRate,
                    convertCurrency(amount, newRate),
                    it.base
                )
            )
        }
        return newData
    }

    private fun convertCurrency(amount: String, rate: BigDecimal): BigDecimal {
        val value = BigDecimal(amount)
        return value.multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP)
    }

    /*
        A country exchange rate to C country is 2
        B country exchange rate to C country is 4
        This function can get A country to B country 's exchange rate = 2 / 4 = 2
     */
    private fun convertRate(targetRate: Double, baseRate: Double): BigDecimal {
        return BigDecimal(targetRate)
            .divide(BigDecimal(baseRate.toString()), 4, RoundingMode.HALF_UP)
    }
}