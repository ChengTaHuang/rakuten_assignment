package com.rakuten.assignment.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponse(
    @SerializedName("rates") @Expose val rates : Map<String, Double>,
    @SerializedName("base") val base : String,
    @SerializedName("date") val date : String
)