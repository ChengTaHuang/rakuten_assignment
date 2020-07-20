package com.rakuten.assignment.activity.splash

import com.rakuten.assignment.BaseTest
import io.reactivex.Flowable
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit


class SplashPresenterTest : BaseTest() {
    private val view = Mockito.mock(SplashContract.View::class.java)
    private val model = Mockito.mock(SplashContract.Model::class.java)
    private lateinit var presenter: SplashContract.Presenter

    @Test
    fun `preparing will finish after 3 second`() {
        presenter = SplashPresenterImpl(model, view)
        Mockito.`when`(model.preparing()).thenReturn(Flowable.interval(3, TimeUnit.SECONDS).onBackpressureDrop())

        presenter.preparing()
        rxjavaTestRule.scheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        Mockito.verify(model).preparing()
        Mockito.verify(view).onFinishPreparing()
    }
}