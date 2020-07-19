package com.rakuten.assignment.custom

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.math.BigDecimal
import java.text.DecimalFormat

class MoneyFormatEditText : AppCompatEditText {
    private var isFormatting: Boolean = false
    private val noDecimalFormat = "###,###"
    private var prevCommaAmount: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if (isFormatting) {
            return
        }

        if (text?.length ?: 0 > 0) {
            isFormatting = true
            formatInput(text!!, start, lengthAfter)
        }

        isFormatting = false
    }

    fun setAllowInput(allowInput: Boolean) {
        var maxLength = Int.MAX_VALUE
        if ((!allowInput)) maxLength = this.text!!.toString().length
        val inputFilterArray = arrayOfNulls<InputFilter>(1)
        inputFilterArray[0] = InputFilter.LengthFilter(maxLength)
        filters = inputFilterArray
    }

    private fun init() {
        setAllowInput(true)
    }

    private fun formatInput(text: CharSequence, start: Int, count: Int) {
        val sbResult = StringBuilder()
        var newStart = start
        try {
            val textWithoutComma = text.toString().replace(",", "")

            //handle .0 situation ( user press "." first)
            if (text.toString().startsWith(".") && text.length == 1) {
                setText("0.")
                setSelection(getText()!!.toString().length)
                return
            }

            if (textWithoutComma.contains(".")) {
                val textList = textWithoutComma.split("\\.".toRegex())
                // textWithoutComma is 100.1 , nonDecimalText is 100
                val nonDecimalText = textList[0]
                if (nonDecimalText.isEmpty()) return

                // only format the non-decimal value
                sbResult.append(formatToStringWithoutDecimal(nonDecimalText))
                    .append(".")

                if (textList.size > 1) {
                    sbResult.append(textList[1])
                }
            } else {
                sbResult.append(formatToStringWithoutDecimal(textWithoutComma))
            }
            // count == 0 indicates users is deleting a text
            // count == 1 indicates users is entering a text
            newStart += if (count == 0) 0 else 1

            // find how many comma in current text
            val commaAmount = sbResult.toString().count { (it == ',') }

            // flag to mark whether new comma is added / removed
            if (commaAmount >= 1 && prevCommaAmount != commaAmount) {
                newStart += if (count == 0) -1 else 1
                prevCommaAmount = commaAmount
            }

            // case when deleting without dots
            if (count == 0 && !sbResult.toString().contains(".") && prevCommaAmount != commaAmount) {
                newStart = start
                prevCommaAmount = commaAmount
            }

            setText(sbResult.toString())

            // ensure newStart is within current text length
            if (newStart > sbResult.toString().length) {
                newStart = sbResult.toString().length
            } else if (newStart < 0) {
                newStart = 0
            }

            setSelection(newStart)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatToStringWithoutDecimal(value: String): String {
        val formatter = DecimalFormat(noDecimalFormat)
        return formatter.format(BigDecimal(value))
    }
}