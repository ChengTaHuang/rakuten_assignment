package com.rakuten.assignment.activity.main

import android.util.Log
import com.rakuten.assignment.base.BaseModelImpl
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Single
import java.math.BigDecimal
import java.math.RoundingMode

class MainModelImpl(private val repo: MainRepository) : BaseModelImpl(),
    MainContract.Model {
    private var countryExchangeRates = mutableListOf<CountryExchangeRate>()
    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return repo.getExchangeRate()
    }

    override fun convertToCountryExchangeRate(data: ExchangeRatesResponse): Single<List<CountryExchangeRate>> {
        if (countryExchangeRates.isEmpty()) {
            countryExchangeRates.addAll(addExchangeData(data.rates, data.base))
        } else {
            updateExchangeRate(data.rates, countryExchangeRates)
        }
        return Single.just(countryExchangeRates)
    }

    override fun getCurrentTime(): String {
        return System.currentTimeMillis().toString()
    }

    override fun changeBaseCountry(iso: String): Single<List<CountryExchangeRate>> {
        val update = mutableListOf<CountryExchangeRate>()
        val countryExchangeRate = countryExchangeRates.find { it.iso == iso }!!
        update.add(countryExchangeRate.copy(base = iso, rate = BigDecimal(1.0.toString())))
        countryExchangeRates
            .filter { it.iso != countryExchangeRate.iso }
            .forEach {
                val newRate = calNewExchangeRate(it.exchangeEUR, countryExchangeRate.exchangeEUR)
                update.add(
                    CountryExchangeRate(
                        it.iso,
                        it.exchangeEUR,
                        newRate,
                        it.amount,
                        countryExchangeRate.iso
                    )
                )
            }
        countryExchangeRates = update
        return Single.just(countryExchangeRates)
    }

    override fun setAmount(amount: String): Single<List<CountryExchangeRate>> {
        val update = mutableListOf<CountryExchangeRate>()
        countryExchangeRates.forEach {
            update.add(CountryExchangeRate(it.iso, it.exchangeEUR, it.rate, calAmount(amount, it.rate), it.base))
        }
        countryExchangeRates = update
        return Single.just(countryExchangeRates)
    }

    private fun addExchangeData(rates: Map<String, Double>, base: String): List<CountryExchangeRate> {
        val countryExchangeRates = mutableListOf<CountryExchangeRate>()
        countryExchangeRates.add(
            CountryExchangeRate(
                base,
                1.0,
                BigDecimal(1.0.toString()),
                BigDecimal(0.0.toString()),
                base
            )
        )
        for ((iso, rate) in rates) {
            countryExchangeRates.add(
                CountryExchangeRate(
                    iso,
                    rate,
                    BigDecimal(rate.toString()),
                    BigDecimal(0.0.toString()),
                    base
                )
            )
        }
        return countryExchangeRates.toList()
    }

    private fun updateExchangeRate(rates: Map<String, Double>, countryExchangeRates: MutableList<CountryExchangeRate>) {

    }

    private fun calAmount(amount: String, rate: BigDecimal): BigDecimal {
        val value = BigDecimal(amount)
        return value.multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP)
    }

    private fun calNewExchangeRate(countryRate: Double, EURRate: Double): BigDecimal {
        return BigDecimal(countryRate)
            .divide(BigDecimal(EURRate.toString()), 4, RoundingMode.HALF_UP)
    }
}