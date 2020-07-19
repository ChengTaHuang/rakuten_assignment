package com.rakuten.assignment.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class TouchTextView : AppCompatTextView {
    private var oriAlpha: Float = 0.0f
    private val tapAlpha = 0.65f
    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        oriAlpha = this.alpha
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                this.alpha = oriAlpha * tapAlpha
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                this.alpha = oriAlpha
            }
        }

        return super.onTouchEvent(event)
    }
}