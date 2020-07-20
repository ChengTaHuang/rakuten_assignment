package com.rakuten.assignment

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.exceptions.CompositeException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.plugins.RxJavaPlugins.*
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.ExternalResource

class RxjavaRule(
    private val setRxImmediateSchedulers: Boolean = true
) : ExternalResource() {
    private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
    var scheduler = TestScheduler()

    override fun before() {
        RxAndroidPlugins.reset()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
        RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler }
        if (setRxImmediateSchedulers) setRxImmediateSchedulers()
    }

    override fun after() {
        RxAndroidPlugins.reset()
        if (setRxImmediateSchedulers) clearRxImmediateSchedulers()
    }

    private fun setRxImmediateSchedulers() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setSingleSchedulerHandler { scheduler }
        //RxJavaPlugins.setInitComputationSchedulerHandler { scheduler }
        //RxJavaPlugins.setInitIoSchedulerHandler { scheduler }
        //RxJavaPlugins.setInitSingleSchedulerHandler { scheduler }
        //RxJavaPlugins.setInitNewThreadSchedulerHandler{ scheduler }
        //RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }

        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            if (e is CompositeException && e.exceptions.size == 1) throw e.exceptions[0]
            throw e
        }
    }

    private fun clearRxImmediateSchedulers() {
        Thread.setDefaultUncaughtExceptionHandler(defaultExceptionHandler)
        defaultExceptionHandler = null
        RxJavaPlugins.reset()
    }
}