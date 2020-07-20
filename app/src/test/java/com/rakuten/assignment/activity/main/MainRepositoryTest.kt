package com.rakuten.assignment.activity.main

import com.rakuten.assignment.BaseTest
import com.rakuten.assignment.service.NetworkService
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit

class MainRepositoryTest : BaseTest() {
    private val network = Mockito.mock(NetworkService::class.java)
    private val repo : MainRepository = MainRepositoryImpl(network)

    @Test
    fun `received 1 signal every 10 seconds`(){
        val test = repo.getTenSecondTimer().test()
        rxjavaTestRule.scheduler.advanceTimeBy(0, TimeUnit.SECONDS)
        test.assertValue(0)
        rxjavaTestRule.scheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        test.assertValues(0 , 1)
        rxjavaTestRule.scheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        test.assertValues(0 , 1 , 2)
    }
}