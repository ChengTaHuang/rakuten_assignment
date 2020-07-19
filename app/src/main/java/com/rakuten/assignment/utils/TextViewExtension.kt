package com.rakuten.assignment.utils

import android.graphics.Rect
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.math.BigDecimal
import java.text.DecimalFormat


fun AppCompatEditText.maximumAmountOfDigital() : Int{
    val bounds = Rect()
    val formatter = DecimalFormat("###,###.####")
    val sb = StringBuffer("9")
    val textPaint = paint
    textPaint.textSize = paint.textSize
    var i = 0
    var textWidth = paint.measureText(sb.toString())
    while(textWidth <= this.measuredWidth){
        val value = formatter.format(BigDecimal(sb.toString()))
        textPaint.getTextBounds(value, 0, value.length, bounds)
        sb.append("9")
        textWidth = paint.measureText(value.toString())
        i+=1
    }
    i--
    return i
}