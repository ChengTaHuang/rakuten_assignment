package com.rakuten.assignment.activity.splash

import com.rakuten.assignment.base.BaseContract
import io.reactivex.Flowable

class SplashContract {
    interface Model : BaseContract.Model {
        fun preparing() : Flowable<Long>
    }

    interface View : BaseContract.View {
        fun onFinishPreparing()
    }

    interface Presenter {
        fun preparing()
    }
}