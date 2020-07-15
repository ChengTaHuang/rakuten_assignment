package com.rakuten.assignment.activity.main

import com.rakuten.assignment.service.NetworkService

interface MainRepository {

}

class MainRepositoryImpl(private val service: NetworkService) :
    MainRepository {

}