package com.rakuten.assignment.activity

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rakuten.assignment.R
import com.rakuten.assignment.base.BaseContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() , BaseContract.View {
    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun bind(disposable: Disposable) {
        disposables.add(disposable)
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError() {
        Toast.makeText(baseContext , R.string.something_went_wrong , Toast.LENGTH_SHORT).show()
    }
}