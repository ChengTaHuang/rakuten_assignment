package com.rakuten.assignment.activity.splash

import com.rakuten.assignment.rxjava.bind
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SplashPresenterImpl(
    private val model: SplashContract.Model,
    private val view: SplashContract.View
) : SplashContract.Presenter {
    override fun preparing() {
        model.preparing()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.onFinishPreparing()
            }.bind(view)
    }

}