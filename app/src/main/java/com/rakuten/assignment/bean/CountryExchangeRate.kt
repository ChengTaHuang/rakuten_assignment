package com.rakuten.assignment.bean

import java.math.BigDecimal

data class CountryExchangeRate(
    val iso: String,
    val exchangeEUR : Double,
    val rate: BigDecimal,
    val amount: BigDecimal,
    val base: String
)