package com.rakuten.assignment.activity.main

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rakuten.assignment.R
import com.rakuten.assignment.activity.BaseActivity
import com.rakuten.assignment.activity.main.adapter.ExchangeRatesAdapter
import com.rakuten.assignment.bean.CountryExchangeRate
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity(), MainContract.View {

    private val presenter: MainContract.Presenter  by inject { parametersOf(this) }
    private val exchangeRatesAdapter: ExchangeRatesAdapter by lazy { ExchangeRatesAdapter(rvContent) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        presenter.startGettingExchangeRates()
    }

    override fun onPause() {
        super.onPause()
        presenter.stopGettingExchangeRates()
    }

    override fun showExchangeRates(countryExchangeRates: List<CountryExchangeRate>) {
        exchangeRatesAdapter.update(countryExchangeRates)

    }

    override fun showUpdateTime(date: String) {
        tvUpdateTime.text = getString(R.string.update_time , date)
    }

    override fun showTimeLeft(second: Int) {

    }

    override fun showLoading() {
        clpbLoading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        clpbLoading.visibility = View.INVISIBLE
    }

    private fun initViews() {
        with(rvContent) {
            layoutManager = LinearLayoutManager(baseContext)
            adapter = exchangeRatesAdapter
            itemAnimator = null
        }
        setCallBackListener()
    }

    private fun setCallBackListener() {
        exchangeRatesAdapter.setOnAmountChangeListener {
            presenter.setAmount(it)
        }
        exchangeRatesAdapter.setOnBaseCountryChangeListener {
            presenter.setBaseCountry(it)
        }
    }
}
