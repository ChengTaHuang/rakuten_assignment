package com.rakuten.assignment.base

interface BaseContract {

    interface Model {

    }

    interface View {
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