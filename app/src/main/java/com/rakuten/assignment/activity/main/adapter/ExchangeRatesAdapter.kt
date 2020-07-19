package com.rakuten.assignment.activity.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.rakuten.assignment.R
import com.rakuten.assignment.bean.CountryExchangeRate
import com.rakuten.assignment.custom.MoneyFormatEditText
import com.rakuten.assignment.utils.maximumAmountOfDigital
import com.rakuten.assignment.utils.removeAmountLastZero
import com.rakuten.assignment.utils.removeCommaAndDot


class ExchangeRatesAdapter(private val recyclerView: RecyclerView) :
    ListAdapter<ItemData, ExchangeRatesAdapter.BaseViewHolder>(TaskDiffCallback()) {
    private var onAmountChangeListener: ((String) -> Unit)? = null
    private var onBaseCountryChangeListener: ((iso: String) -> Unit)? = null
    private var itemData = mutableListOf<ItemData>()
    //the amount digital of every each item
    private var maximumAmountOfDigital = Int.MAX_VALUE

    init {
        //after base country is changed , recyclerview will scroll to the top
        this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                recyclerView.smoothScrollToPosition(0)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (getItem(viewType)) {
            is ItemData.HeadData -> {
                val onEditTextChangeListener: ((ItemData.HeadData) -> Unit) = {
                    itemData[0] = it
                }
                val onMaximumNumberOfDigitalListener: ((Int) -> Unit) = {
                    if (it in 1 until maximumAmountOfDigital) maximumAmountOfDigital = it
                }

                BaseViewHolder.HeadViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate_head,
                        parent,
                        false
                    ),
                    onEditTextChangeListener,
                    onMaximumNumberOfDigitalListener,
                    onAmountChangeListener
                )
            }
            is ItemData.BodyData -> {
                val onBaseCountryChangeListener: ((iso: String) -> Unit) = {
                    //reset input amount to empty
                    (itemData[0] as ItemData.HeadData).inputAmount = ""
                    onBaseCountryChangeListener?.invoke(it)
                }

                BaseViewHolder.BodyViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate_body,
                        parent,
                        false
                    ),
                    onBaseCountryChangeListener
                )
            }
            else -> throw Exception("NO SUPPORT THIS VIEW TYPE")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is BaseViewHolder.HeadViewHolder -> holder.render(itemData[position] as ItemData.HeadData)
            is BaseViewHolder.BodyViewHolder -> holder.render(itemData[position] as ItemData.BodyData)
        }
    }

    fun update(data: List<CountryExchangeRate>) {
        itemData = convertToItemData(data, maximumAmountOfDigital)
        submitList(itemData.toMutableList())
    }

    fun setOnAmountChangeListener(listener: ((String) -> Unit)) {
        onAmountChangeListener = listener
    }

    fun setOnBaseCountryChangeListener(listener: ((iso: String) -> Unit)) {
        onBaseCountryChangeListener = listener
    }

    private fun convertToItemData(data: List<CountryExchangeRate>, maximumNumberOfDigital: Int): MutableList<ItemData> {
        val newItemData = mutableListOf<ItemData>()
        val maximumDigitalOfCurrencyLength = data.maxBy {
            it.amount.toPlainString().removeAmountLastZero().removeCommaAndDot().length
        }?.amount?.toPlainString()?.removeAmountLastZero()?.removeCommaAndDot()?.length ?: 0

        data.forEach { countryExchangeRate ->
            if (newItemData.isEmpty()) {
                //if it's first time convert to item data input amount will be empty else use the last input amount
                val inputAmount =
                    if (this.itemData.isNotEmpty()) (this.itemData[0] as ItemData.HeadData).inputAmount else ""
                //remove all comma and dot by input amount
                val onlyDigitalInputAmount = inputAmount.removeCommaAndDot().length

                val isAlreadyFitEditText =
                    (onlyDigitalInputAmount >= maximumNumberOfDigital || maximumDigitalOfCurrencyLength >= maximumNumberOfDigital)

                newItemData.add(ItemData.HeadData(countryExchangeRate, inputAmount, isAlreadyFitEditText))
            } else newItemData.add(
                ItemData.BodyData(
                    countryExchangeRate,
                    countryExchangeRate.amount.toPlainString().removeAmountLastZero()
                )
            )
        }
        return newItemData
    }

    sealed class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        protected val imgFlag: AppCompatImageView by lazy { itemView.findViewById<AppCompatImageView>(R.id.imgFlag) }
        protected val tvRate: AppCompatTextView by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvRate) }
        protected val tvCountryName: AppCompatTextView by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvCountryName) }
        protected val editAmount: MoneyFormatEditText by lazy { itemView.findViewById<MoneyFormatEditText>(R.id.editAmount) }
        protected val clBackground: ConstraintLayout by lazy { itemView.findViewById<ConstraintLayout>(R.id.clBackground) }
        protected val keyboardManager: InputMethodManager by lazy {
            itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        protected fun render(data: ItemData) {
            val currency = ExtendedCurrency.getCurrencyByISO(data.countryExchangeRate.iso)
            imgFlag.setImageResource(currency.flag)
            tvRate.text = itemView.context.getString(
                R.string.exchange_rate_label,
                data.countryExchangeRate.base,
                data.countryExchangeRate.rate.toString().removeAmountLastZero(),
                data.countryExchangeRate.iso
            )
            tvCountryName.text = data.countryExchangeRate.iso
        }

        data class HeadViewHolder(
            val view: View,
            val onEditTextChangeListener: ((ItemData.HeadData) -> Unit),
            val onMaximumAmountOfDigitalListener: ((Int) -> Unit),
            val onAmountChangeListener: ((String) -> Unit)?
        ) : BaseViewHolder(view) {
            @SuppressLint("ClickableViewAccessibility")
            fun render(data: ItemData.HeadData) {
                super.render(data)
                editAmount.setOnTouchListener { _, _ ->
                    setEditAble(editAmount)
                    true
                }
                editAmount.setOnFocusChangeListener { view, focus ->
                    if (!focus) {
                        keyboardManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
                editAmount.setAllowInput(!data.isAmountFitEditText)
                editAmount.setText(data.inputAmount)
                editAmount.setSelection(editAmount.text.toString().length)
                editAmount.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(editable: Editable?) {
                        editable?.toString()?.let {text ->
                            val cleanAmount = text.removeCommaAndDot()
                            val withoutCommaAmount = text.replace("[,]".toRegex(), "")
                            onAmountChangeListener?.invoke(if (cleanAmount.isEmpty()) "0.0" else withoutCommaAmount)
                            onEditTextChangeListener.invoke(data.copy(inputAmount = text))
                            editAmount.removeTextChangedListener(this)
                        }
                    }

                    override fun beforeTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })
                editAmount.setOnKeyListener(object : View.OnKeyListener {
                    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                        //if EditText does not allow entering more numbers, it will be shake
                        if (event.action == KeyEvent.ACTION_DOWN && data.isAmountFitEditText) {
                            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                                editAmount.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.shake))
                                return true
                            }
                        }
                        return false
                    }
                })
                onMaximumAmountOfDigitalListener.invoke(editAmount.maximumAmountOfDigital())
                clBackground.setOnClickListener {
                    setEditAble(editAmount)
                }
            }

            private fun setEditAble(editText: EditText) {
                editText.requestFocus()
                keyboardManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                editText.setSelection(editText.text.toString().length)
            }
        }

        data class BodyViewHolder(
            val view: View,
            val onBaseCountryChangeListener: ((iso: String) -> Unit)?
        ) : BaseViewHolder(view) {
            fun render(data: ItemData.BodyData) {
                super.render(data)
                editAmount.clearFocus()
                editAmount.isEnabled = false
                imgFlag.isEnabled = false
                tvRate.isEnabled = false
                tvCountryName.isEnabled = false
                val amount = if (data.currency.toDouble() == 0.0) "" else {
                    String.format(data.currency)
                }
                editAmount.setText(amount)
                editAmount.setSelection(editAmount.length())
                clBackground.setOnClickListener {
                    onBaseCountryChangeListener?.invoke(data.countryExchangeRate.iso)
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem.countryExchangeRate.iso == newItem.countryExchangeRate.iso
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return if (oldItem is ItemData.HeadData && newItem is ItemData.HeadData) {
                oldItem.inputAmount == newItem.inputAmount && oldItem.countryExchangeRate == newItem.countryExchangeRate
            } else oldItem.countryExchangeRate == newItem.countryExchangeRate
        }
    }
}