package com.rakuten.assignment.koin

import com.rakuten.assignment.activity.main.*
import com.rakuten.assignment.activity.splash.*
import com.rakuten.assignment.service.NetworkService
import com.rakuten.assignment.service.NetworkServiceImpl
import org.koin.dsl.module

val appModule = module {
    single<NetworkService> {
        NetworkServiceImpl(get())
    }
    single<MainRepository> {
        MainRepositoryImpl(get())
    }
    factory<MainContract.Model> {
        MainModelImpl(get())
    }
    factory<MainContract.Presenter> { (view: MainContract.View) ->
        MainPresenterImpl(get(), view)
    }
    factory<SplashContract.Model> {
        SplashModelImpl()
    }
    factory<SplashContract.Presenter> { (view: SplashContract.View) ->
        SplashPresenterImpl(get(), view)
    }
}