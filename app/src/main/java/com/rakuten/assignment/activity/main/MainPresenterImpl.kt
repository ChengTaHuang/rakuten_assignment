package com.rakuten.assignment.activity.main

import com.rakuten.assignment.rxjava.bind
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainPresenterImpl(
    private val model: MainContract.Model,
    private val view: MainContract.View
) : MainContract.Presenter {
    private val timerDisposable = CompositeDisposable()

    override fun startGettingExchangeRates() {
        timerListener {
            model.isNetworkConnected()
                .flatMap { isConnected ->
                    if (isConnected) model.getExchangeRate()
                    else throw NetworkConnectException()
                }
                .subscribeOn(Schedulers.io())
                .flatMap {
                    model.convertToCountryExchangeRate(it)
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showLoading() }
                .doFinally { view.hideLoading() }
                .subscribe({
                    view.hideNetworkConnectionError()
                    view.showExchangeRates(it)
                    view.showUpdateTime(model.getCurrentTime())
                }, {
                    if (it is NetworkConnectException) {
                        stopGettingExchangeRates()
                        view.showNetworkConnectionError()
                    } else {
                        view.showError()
                    }
                }).bind(view)
        }
    }

    override fun stopGettingExchangeRates() {
        timerDisposable.clear()
    }

    override fun setBaseCountry(iso: String) {
        model.changeBaseCountryAndResetAmount(iso)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.showExchangeRates(it)
            }, {
                view.showError()
            }).bind(view)
    }

    override fun setAmount(amount: String) {
        model.getNewCountryExchangeRateByAmount(amount)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.showExchangeRates(it)
            }, {
                view.showError()
            }).bind(view)
    }

    private fun timerListener(callBack: () -> Unit) {
        timerDisposable.clear()
        timerDisposable.add(model.getTenSecondTimer()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callBack()
            })
    }

    class NetworkConnectException : Exception()
}