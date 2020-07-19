package com.rakuten.assignment.bean

import java.math.BigDecimal

data class CountryExchangeRate(
    val iso: String,
    val eurExchangeRate : Double,
    val rate: BigDecimal,
    val amount: BigDecimal,
    val base: String
)