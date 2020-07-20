package com.rakuten.assignment.activity.splash

import com.rakuten.assignment.BaseTest
import io.reactivex.Flowable
import io.reactivex.Observable
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito
import java.util.concurrent.TimeUnit
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber


class SplashModelTest : BaseTest() {

    private val model : SplashContract.Model = SplashModelImpl()

    @Test
    fun `every 3 seconds return new value`() {
        val test = model.preparing().test()
        rxjavaTestRule.scheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        test.assertValue(0)
        rxjavaTestRule.scheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        test.assertValues(0 , 1)
    }
}