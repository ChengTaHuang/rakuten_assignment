package com.rakuten.assignment.activity.splash

import com.rakuten.assignment.base.BaseModelImpl
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class SplashModelImpl() : BaseModelImpl(),
    SplashContract.Model {
    override fun preparing(): Flowable<Long> {
        return Flowable.interval( 3, TimeUnit.SECONDS).onBackpressureDrop()
    }

}