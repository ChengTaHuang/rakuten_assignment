package com.rakuten.assignment.activity.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynameismidori.currencypicker.ExtendedCurrency
import com.rakuten.assignment.R
import com.rakuten.assignment.bean.CountryExchangeRate


class ExchangeRatesAdapter() :
    ListAdapter<CountryExchangeRate, ExchangeRatesAdapter.BaseViewHolder>(TaskDiffCallback()) {
    private val typeHead = 0
    private val typeBody = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeHead -> {
                BaseViewHolder.ViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate,
                        parent,
                        false
                    )
                )
            }
            typeBody -> {
                BaseViewHolder.ViewHolder(
                    inflater.inflate(
                        R.layout.item_country_rate,
                        parent,
                        false
                    )
                )
            }
            else -> throw Exception("NOT SUPPORT THIS VIEW TYPE")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        (holder as BaseViewHolder.ViewHolder).render(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == typeHead) typeHead else typeBody
    }

    fun update(data: List<CountryExchangeRate>) {
        submitList(data)
    }

    sealed class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgFlag by lazy { itemView.findViewById<AppCompatImageView>(R.id.imgFlag) }
        private val tvRate by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvRate) }
        private val tvCountryName by lazy { itemView.findViewById<AppCompatTextView>(R.id.tvCountryName) }
        open fun render(data: CountryExchangeRate) {
            val currency = ExtendedCurrency.getCurrencyByISO(data.iso)
            imgFlag.setImageResource(currency.flag)
            tvRate.text = itemView.context.getString(R.string.exchange_rate_label, data.iso, data.baseRate, data.base)
            tvCountryName.text = data.iso
        }

        data class ViewHolder(val view: View) : BaseViewHolder(view) {

            override fun render(data: CountryExchangeRate) {
                super.render(data)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<CountryExchangeRate>() {
        override fun areItemsTheSame(oldItem: CountryExchangeRate, newItem: CountryExchangeRate): Boolean {
            return oldItem.iso == newItem.iso
        }

        override fun areContentsTheSame(oldItem: CountryExchangeRate, newItem: CountryExchangeRate): Boolean {
            return oldItem == newItem
        }
    }
}