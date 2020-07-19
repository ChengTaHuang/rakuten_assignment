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
import com.rakuten.assignment.custom.AutoFormatEditText2
import com.rakuten.assignment.custom.MoneyFormatEditText
import com.rakuten.assignment.utils.howManyNumberCanItPut
import com.rakuten.assignment.utils.removeAmountLastZero


class ExchangeRatesAdapter(private val recyclerView: RecyclerView) :
    ListAdapter<ItemData, ExchangeRatesAdapter.BaseViewHolder>(TaskDiffCallback()) {
    private val typeHead = 0
    private val typeBody = 1
    private var onAmountChangeListener: ((String) -> Unit)? = null
    private var onBaseCountryChangeListener: ((iso: String) -> Unit)? = null
    private var onIsFitAmountEditTextListener: ((Boolean) -> Unit)? = null
    private var itemData = mutableListOf<ItemData>()
    private var maximumNumberOfDigital = Int.MAX_VALUE

    init {
        this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                recyclerView.smoothScrollToPosition(0)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeHead -> {
                BaseViewHolder.HeadViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate_head,
                        parent,
                        false
                    ),
                    {
                        itemData[0] = it
                    },
                    {
                        if (it in 1 until maximumNumberOfDigital) maximumNumberOfDigital = it
                    },
                    onAmountChangeListener
                )
            }
            typeBody -> {
                BaseViewHolder.BodyViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate_body,
                        parent,
                        false
                    )
                ) {
                    (itemData[0] as ItemData.HeadData).input = ""
                    onBaseCountryChangeListener?.invoke(it)
                }
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

    override fun getItemViewType(position: Int): Int {
        return if (position == typeHead) typeHead else typeBody
    }

    fun update(data: List<CountryExchangeRate>) {
        itemData = convertToItemData(data, maximumNumberOfDigital)
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
        val maxAmountLength = data.maxBy {
            it.amount.toPlainString().removeAmountLastZero().replace("[,.]".toRegex(), "").length
        }?.amount?.toPlainString()?.removeAmountLastZero()?.replace("[,.]".toRegex(), "")?.length ?: 0

        data.forEach {
            if (newItemData.isEmpty()) {
                val input = if (this.itemData.isNotEmpty()) (this.itemData[0] as ItemData.HeadData).input else ""
                val onlyDigitalInput = input.replace("[,.]".toRegex(), "").length
                val isFit = (onlyDigitalInput >= maximumNumberOfDigital || maxAmountLength >= maximumNumberOfDigital)
                newItemData.add(ItemData.HeadData(it, input, isFit))
            } else newItemData.add(ItemData.BodyData(it, it.amount.toPlainString().removeAmountLastZero()))
        }
        return newItemData
    }

    sealed class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        protected val imgFlag by lazy { itemView.findViewById<AppCompatImageView>(R.id.imgFlag) }
        protected val tvRate by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvRate) }
        protected val tvCountryName by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvCountryName) }
        protected val editAmount by lazy { itemView.findViewById<MoneyFormatEditText>(R.id.editAmount) }
        protected val clBackground by lazy { itemView.findViewById<ConstraintLayout>(R.id.clBackground) }
        protected val keyboardManager: InputMethodManager by lazy {
            itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        open fun render(data: ItemData) {
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
            val onIsFitAmountEditTextListener: ((Int) -> Unit),
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
                editAmount.setText(data.input)
                editAmount.setSelection(editAmount.text.toString().length)
                editAmount.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        text?.let {
                            val cleanAmount = text.toString().replace("[,.]".toRegex(), "")
                            val withOutCommaAmount = text.toString().replace("[,]".toRegex(), "")
                            onAmountChangeListener?.invoke(if (cleanAmount.isEmpty()) "0.0" else withOutCommaAmount)
                            onEditTextChangeListener.invoke(data.copy(input = it.toString()))
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
                        if (event.action == KeyEvent.ACTION_DOWN && data.isAmountFitEditText) {
                            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                                editAmount.startAnimation(AnimationUtils.loadAnimation(v.context, R.anim.shake))
                                return true
                            }
                        }
                        return false
                    }
                })
                onIsFitAmountEditTextListener.invoke(editAmount.howManyNumberCanItPut())
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
                val amount = if (data.print.toDouble() == 0.0) "" else {
                    String.format(data.print)
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
            if (oldItem is ItemData.HeadData && newItem is ItemData.HeadData) {
                return oldItem.input == newItem.input && oldItem.countryExchangeRate == newItem.countryExchangeRate
            } else return oldItem.countryExchangeRate == newItem.countryExchangeRate
        }
    }
}