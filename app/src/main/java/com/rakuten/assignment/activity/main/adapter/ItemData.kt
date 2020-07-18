package com.rakuten.assignment.activity.main.adapter

import com.rakuten.assignment.bean.CountryExchangeRate

sealed class ItemData(val countryExchangeRate: CountryExchangeRate){
    data class HeadData(private val _countryExchangeRate : CountryExchangeRate, var input : String) : ItemData(_countryExchangeRate)
    data class BodyData(private val _countryExchangeRate : CountryExchangeRate , val print : String) : ItemData(_countryExchangeRate)
}