package com.rakuten.assignment

import org.junit.ClassRule

abstract class BaseTest {

    companion object {

        @JvmField
        @ClassRule
        val rxjavaTestRule = RxjavaRule()
    }
}