package com.rakuten.assignment.activity.main

import com.rakuten.assignment.rxjava.bind
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainPresenterImpl(
    private val model: MainContract.Model,
    private val view: MainContract.View
) : MainContract.Presenter {
    override fun startGettingExchangeRates() {
        model.getExchangeRate()
            .subscribeOn(Schedulers.io())
            .flatMap {
                model.convertToCountryExchangeRate(it)
            }
            .doAfterSuccess {
                view.showUpdateTime(model.getCurrentTime())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.showExchangeRates(it)
            }, {
                view.showError()
            }).bind(view)
    }

    override fun stopGettingExchangeRates() {

    }

    override fun setAmount(amount: Double) {
        model.setAmount(amount)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.showExchangeRates(it)
            },{
                view.showError()
            }).bind(view)
    }
}