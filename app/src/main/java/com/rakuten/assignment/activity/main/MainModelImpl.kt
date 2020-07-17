package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseModelImpl
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.bean.ExchangeRatesResponse
import io.reactivex.Single
import java.math.BigDecimal

class MainModelImpl(private val repo: MainRepository) : BaseModelImpl(),
    MainContract.Model {
    private val countryExchangeRates = mutableListOf<CountryExchangeRate>()
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

    private fun addExchangeData(rates: Map<String, Double>, base: String): List<CountryExchangeRate> {
        val countryExchangeRates = mutableListOf<CountryExchangeRate>()
        countryExchangeRates.add(CountryExchangeRate(base, 1.0, BigDecimal(0.0), base))
        for ((iso, rate) in rates) {
            countryExchangeRates.add(CountryExchangeRate(iso, rate, BigDecimal(0.0), base))
        }
        return countryExchangeRates.toList()
    }

    private fun updateExchangeRate(rates: Map<String, Double>, countryExchangeRates: MutableList<CountryExchangeRate>) {
//        val updateData = countryExchangeRates.map { it.copy() }.toMutableList().apply {
//            this.forEachIndexed { index, countryExchangeRate ->
//                if (rates.containsKey(countryExchangeRate.iso)) {
//                    set(index, countryExchangeRate.copy(rate = rates.getValue(countryExchangeRate.iso)))
//                }
//            }
//        }.toMutableList()
        val updateData = countryExchangeRates.apply {
            this.forEachIndexed { index, countryExchangeRate ->
                if (rates.containsKey(countryExchangeRate.iso)) {
                    set(index, countryExchangeRate.copy(rate = rates.getValue(countryExchangeRate.iso)))
                }
            }
        }.toMutableList()
    }
}