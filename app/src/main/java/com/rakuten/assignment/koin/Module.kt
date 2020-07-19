package com.rakuten.assignment.koin

import com.rakuten.assignment.activity.main.*
import com.rakuten.assignment.activity.splash.*
import com.rakuten.assignment.service.NetworkService
import org.koin.dsl.module

val appModule = module {
    single {
        NetworkService(get())
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
    single<SplashRepository> {
        SplashRepositoryImpl(get())
    }
    factory<SplashContract.Model> {
        SplashModelImpl(get())
    }
    factory<SplashContract.Presenter> { (view: SplashContract.View) ->
        SplashPresenterImpl(get(), view)
    }
}