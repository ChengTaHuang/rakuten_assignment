package com.rakuten.assignment.activity.main

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.rakuten.assignment.R
import com.rakuten.assignment.activity.BaseActivity
import com.rakuten.assignment.activity.main.adapter.ExchangeRatesAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private val exchangeRatesAdapter : ExchangeRatesAdapter = ExchangeRatesAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun initViews(){
        with(rvContent){
            layoutManager = LinearLayoutManager(baseContext)
            adapter = exchangeRatesAdapter
        }

        val demo = mutableListOf<Int>()
        demo.add(1)
        demo.add(1)
        demo.add(1)
        demo.add(1)
        demo.add(1)
        demo.add(1)
        exchangeRatesAdapter.update(demo)
    }
}
