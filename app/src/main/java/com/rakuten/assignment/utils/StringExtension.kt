package com.rakuten.assignment.utils

fun String.removeAmountLastZero(): String {
    var amount = this
    if (this.indexOf(".") > 0) {
        amount = amount.replace("0+?$".toRegex(), "")
        amount = amount.replace("[.]$".toRegex(), "")
    }
    return amount
}

fun String.removeCommaAndDot(): String {
    return this.replace("[,.]".toRegex(), "")
}