package com.rakuten.assignment.activity.main

import com.rakuten.assignment.rxjava.bind
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainPresenterImpl(
    private val model: MainContract.Model,
    private val view: MainContract.View
) : MainContract.Presenter {
    private val timerDisposable = CompositeDisposable()

    override fun startGettingExchangeRates() {
        timerListener({
            view.showTimeLeft(it)
        } , {
            model.isNetworkConnected()
                .flatMap { isConnected ->
                    if(isConnected) model.getExchangeRate()
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
        })
    }

    override fun stopGettingExchangeRates() {
        timerDisposable.clear()
    }

    override fun setBaseCountry(iso: String) {
        model.changeBaseCountry(iso)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.showExchangeRates(it)
            }, {
                view.showError()
            }).bind(view)
    }

    override fun setAmount(amount: String) {
        model.setAmount(amount)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view.showExchangeRates(it)
            }, {
                view.showError()
            }).bind(view)
    }

    private fun timerListener(second : (Int) -> Unit , callBack : () -> Unit){
        timerDisposable.clear()
        timerDisposable.add(Flowable.interval(0, 1, TimeUnit.SECONDS)
            .onBackpressureDrop()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it.toInt() % 10 == 0){
                    callBack()
                }
                second(it.toInt() % 10)
            })
    }

    class NetworkConnectException : Exception()
}