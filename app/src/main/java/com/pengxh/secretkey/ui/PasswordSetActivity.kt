package com.pengxh.secretkey.ui

import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.DigitKeyboard
import com.pengxh.secretkey.widgets.PasswordEditText
import kotlinx.android.synthetic.main.activity_setpassword.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/25 21:29
 */
class PasswordSetActivity : BaseNormalActivity(), DigitKeyboard.DigitKeyboardClickListener,
    PasswordEditText.OnFinishListener {

    companion object {
        private const val Tag: String = "PasswordSetActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_setpassword

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "设置密码"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        mTitleLeftView.setOnClickListener { this.finish() }

        digitKeyboard.setOnDigitKeyboardClickListener(this)
        passwordEditText.setOnFinishListener(this)

        passwordEditText.isEnabled = true
        passwordEditText.isFocusable = false
        passwordEditText.isFocusableInTouchMode = false
        passwordEditText.setOnClickListener { digitKeyboard.visibility = View.VISIBLE }
    }

    override fun onClick(number: String?) {
        if (number != null) {
            passwordEditText.addPassword(number)
        }
    }

    override fun onDelete() {
        passwordEditText.deleteLastPassword()
    }

    override fun onPasswordFinish(password: String?) {
        Log.d(Tag, ": $password")
    }

    override fun onPasswordChanged(password: String?) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val isConsumer: Boolean = digitKeyboard.dispatchKeyEventInFullScreen(event)
        return if (isConsumer) isConsumer else super.onKeyDown(keyCode, event)
    }
}