package com.rakuten.assignment.rxjava

import com.rakuten.assignment.base.BaseContract
import io.reactivex.disposables.Disposable


fun Disposable.bind(view: BaseContract.View) {
    view.bind(this)
}