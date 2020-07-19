package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseModelImpl
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import com.rakuten.assignment.utils.TimeFormat
import io.reactivex.Single
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class MainModelImpl(private val repo: MainRepository) : BaseModelImpl(),
    MainContract.Model {
    private var countryExchangeRates = mutableListOf<CountryExchangeRate>()
    private var currentAmount: String = "0"

    override fun isNetworkConnected(): Single<Boolean> {
        return repo.isNetworkConnected()
    }

    override fun getExchangeRate(): Single<ExchangeRatesResponse> {
        return repo.getExchangeRate()
    }

    //if countryExchangeRates is empty , give it new data , else recalculate amount with new rate
    override fun convertToCountryExchangeRate(data: ExchangeRatesResponse): Single<List<CountryExchangeRate>> {
        if (countryExchangeRates.isEmpty()) {
            countryExchangeRates.addAll(addExchangeData(data.rates, data.base))
        } else {
            countryExchangeRates = updateExchangeRate(data.rates, currentAmount)
        }
        return Single.just(countryExchangeRates)
    }

    override fun getCurrentTime(): String {
        return TimeFormat.time(Date(System.currentTimeMillis()))
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
                        BigDecimal(0.0.toString()),
                        countryExchangeRate.iso
                    )
                )
            }
        countryExchangeRates = update
        return Single.just(countryExchangeRates)
    }

    override fun setAmount(amount: String): Single<List<CountryExchangeRate>> {
        this.currentAmount = amount
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
                1.00,
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

    private fun updateExchangeRate(rates: Map<String, Double>, amount: String): MutableList<CountryExchangeRate> {
        val update = mutableListOf<CountryExchangeRate>()
        val baseCountryExchangeRates = countryExchangeRates[0]
        countryExchangeRates.forEach {
            val exchangeEUR = if (rates[it.iso] == null) 1.0 else rates[it.iso]!!
            val newRate = calNewExchangeRate(exchangeEUR, baseCountryExchangeRates.exchangeEUR)
            update.add(
                CountryExchangeRate(
                    it.iso,
                    exchangeEUR,
                    newRate,
                    calAmount(amount, newRate),
                    it.base
                )
            )
        }
        return update
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