package com.pengxh.secretkey.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MarqueeTextView(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {

    override fun isFocused(): Boolean {
        return true //自动获取焦点
    }
}