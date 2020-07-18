package com.rakuten.assignment.activity.main.adapter

import com.rakuten.assignment.bean.CountryExchangeRate

sealed class ItemData(open val countryExchangeRate: CountryExchangeRate) {
    data class HeadData(
        override val countryExchangeRate: CountryExchangeRate,
        var input: String,
        var isAmountFitEditText: Boolean
    ) : ItemData(countryExchangeRate)

    data class BodyData(
        override val countryExchangeRate: CountryExchangeRate,
        val print: String
    ) : ItemData(countryExchangeRate)
}