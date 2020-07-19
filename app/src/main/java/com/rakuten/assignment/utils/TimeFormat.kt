package com.rakuten.assignment.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeFormat {
    @SuppressLint("SimpleDateFormat")
    private var format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    fun time(date : Date) : String{
        return format.format(date)
    }
}