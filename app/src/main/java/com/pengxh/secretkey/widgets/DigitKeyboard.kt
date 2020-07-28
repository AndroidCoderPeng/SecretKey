package com.pengxh.secretkey.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.pengxh.secretkey.R

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/28 10:13
 */
class DigitKeyboard @JvmOverloads constructor(context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var mListener: DigitKeyboardClickListener? = null

    init {
        View.inflate(context, R.layout.digit_keyboard, this)
        setChildViewOnclick(this)
    }

    /**
     * 设置键盘子View的点击事件
     */
    private fun setChildViewOnclick(parent: ViewGroup) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            // 不断的递归设置点击事件
            val view = parent.getChildAt(i)
            if (view is ViewGroup) {
                setChildViewOnclick(view)
                continue
            }
            view.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        if (v is TextView) {
            // 如果点击的是TextView
            val number = v.text.toString()
            if (!TextUtils.isEmpty(number)) {
                if (mListener != null) {
                    mListener!!.onClick(number)
                }
            }
        } else if (v is ImageView) {
            // 如果是图片那肯定点击的是删除
            if (mListener != null) {
                mListener!!.onDelete()
            }
        }
    }

    fun dispatchKeyEventInFullScreen(event: KeyEvent?): Boolean {
        if (event == null) {
            return false
        }
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShown) {
                visibility = View.GONE
                return true
            }
        }
        return false
    }

    /**
     * 设置键盘的点击回调监听
     */
    fun setOnDigitKeyboardClickListener(listener: DigitKeyboardClickListener?) {
        mListener = listener
    }

    /**
     * 点击键盘的回调监听
     */
    interface DigitKeyboardClickListener {
        fun onClick(number: String?)
        fun onDelete()
    }
}