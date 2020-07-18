package com.rakuten.assignment.activity.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aldoapps.autoformatedittext.AutoFormatEditText
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.rakuten.assignment.R
import com.rakuten.assignment.bean.CountryExchangeRate

class ExchangeRatesAdapter(val recyclerView: RecyclerView) :
    ListAdapter<ItemData, ExchangeRatesAdapter.BaseViewHolder>(TaskDiffCallback()) {
    private val typeHead = 0
    private val typeBody = 1
    private var onAmountChangeListener: ((Double) -> Unit)? = null
    private var itemData = mutableListOf<ItemData>()
    private val keyboardManager: InputMethodManager by lazy {
        recyclerView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    keyboardManager.hideSoftInputFromWindow(recyclerView.windowToken, 0)
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeHead -> {
                BaseViewHolder.HeadViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate,
                        parent,
                        false
                    ),
                    {
                        itemData[0] = it
                    },
                    onAmountChangeListener
                )
            }
            typeBody -> {
                BaseViewHolder.BodyViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate,
                        parent,
                        false
                    )
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

    override fun getItemViewType(position: Int): Int {
        return if (position == typeHead) typeHead else typeBody
    }

    fun update(data: List<CountryExchangeRate>) {
        itemData = convertToItemData(data)
        submitList(itemData)
    }

    fun setOnAmountChangeListener(listener: ((Double) -> Unit)) {
        onAmountChangeListener = listener
    }

    private fun convertToItemData(data: List<CountryExchangeRate>): MutableList<ItemData> {
        val newItemData = mutableListOf<ItemData>()
        data.forEach {
            val input = if (this.itemData.isNotEmpty()) (this.itemData[0] as ItemData.HeadData).input else ""
            if (newItemData.isEmpty()) newItemData.add(ItemData.HeadData(it, input))
            else newItemData.add(ItemData.BodyData(it))
        }
        return newItemData
    }

    sealed class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgFlag by lazy { itemView.findViewById<AppCompatImageView>(R.id.imgFlag) }
        private val tvRate by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvRate) }
        private val tvCountryName by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvCountryName) }
        protected val editAmount by lazy { itemView.findViewById<AutoFormatEditText>(R.id.editAmount) }
        protected val keyboardManager: InputMethodManager by lazy {
            itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        open fun render(data: ItemData) {
            val currency = ExtendedCurrency.getCurrencyByISO(data.countryExchangeRate.iso)
            imgFlag.setImageResource(currency.flag)
            tvRate.text = itemView.context.getString(
                R.string.exchange_rate_label,
                data.countryExchangeRate.iso,
                data.countryExchangeRate.baseRate,
                data.countryExchangeRate.base
            )
            tvCountryName.text = data.countryExchangeRate.iso
        }

        data class HeadViewHolder(
            val view: View,
            val onEditTextChangeListener: ((ItemData.HeadData) -> Unit),
            val onAmountChangeListener: ((Double) -> Unit)?
        ) : BaseViewHolder(view) {
            private val clBackground by lazy { itemView.findViewById<ConstraintLayout>(R.id.clBackground) }
            @SuppressLint("ClickableViewAccessibility")
            fun render(data: ItemData.HeadData) {
                super.render(data)
                editAmount.setOnTouchListener { _, _ ->
                    setEditAble(editAmount)
                    true
                }
                editAmount.setText(data.input)
                editAmount.setSelection(editAmount.text.toString().length)
                editAmount.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {
                        text?.let {
                            val cleanAmount = text.toString().replace(",.".toRegex(), "")
                            onAmountChangeListener?.invoke(if (cleanAmount.isEmpty()) 0.0 else cleanAmount.toDouble())
                            onEditTextChangeListener.invoke(data.copy(input = it.toString()))
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                })
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

        data class BodyViewHolder(val view: View) : BaseViewHolder(view) {

            override fun render(data: ItemData) {
                super.render(data)
                editAmount.isEnabled = false
                editAmount.setText(data.countryExchangeRate.amount.toString())
                editAmount.setSelection(editAmount.length())
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<ItemData>() {
        override fun areItemsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem.countryExchangeRate.iso == newItem.countryExchangeRate.iso
        }

        override fun areContentsTheSame(oldItem: ItemData, newItem: ItemData): Boolean {
            return oldItem.countryExchangeRate == newItem.countryExchangeRate
        }
    }
}