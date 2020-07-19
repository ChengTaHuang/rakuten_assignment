package com.rakuten.assignment.activity.splash

import android.os.Bundle
import com.rakuten.assignment.R
import com.rakuten.assignment.activity.BaseActivity
import com.rakuten.assignment.activity.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SplashActivity : BaseActivity(), SplashContract.View {
    private val presenter: SplashContract.Presenter  by inject { parametersOf(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        presenter.preparing()
    }

    override fun onFinishPreparing() {
        MainActivity.startActivity(this)
        finish()
    }
}
