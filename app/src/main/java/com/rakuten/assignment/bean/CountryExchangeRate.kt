package com.rakuten.assignment.bean

import java.math.BigDecimal

data class CountryExchangeRate(
    val iso: String,
    val rate: Double,
    val amount: BigDecimal,
    val base: String
) {
    val baseRate: Double = 1 / rate
}