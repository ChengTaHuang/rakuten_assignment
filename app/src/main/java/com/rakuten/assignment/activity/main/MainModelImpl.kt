package com.rakuten.assignment.activity.main

import com.rakuten.assignment.base.BaseModelImpl

class MainModelImpl(private val repo: MainRepository) : BaseModelImpl(),
    MainContract.Model {

}