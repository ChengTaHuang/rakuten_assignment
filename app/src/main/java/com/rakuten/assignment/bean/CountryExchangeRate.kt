package com.rakuten.assignment.bean

data class CountryExchangeRate(
    val iso: String,
    val rate: Double,
    val amount: Double,
    val base: String
) {
    val baseRate: Double = 1 / rate
}