package com.rakuten.assignment.base

import io.reactivex.disposables.Disposable

interface BaseContract {

    interface Model {

    }

    interface View {
        fun bind(disposable: Disposable)

        fun showLoading()

        fun hideLoading()

        fun showError()
    }

    interface Presenter {

    }

}

open class BaseModelImpl() : BaseContract.Model {

}

open class BasePresenterImpl(
    private val model: BaseContract.Model,
    private val view: BaseContract.View
) : BaseContract.Presenter {
}