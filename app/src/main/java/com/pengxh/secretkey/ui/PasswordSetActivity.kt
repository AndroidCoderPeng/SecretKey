package com.pengxh.secretkey.ui

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.widgets.DigitKeyboard
import com.pengxh.secretkey.widgets.PasswordEditText
import kotlinx.android.synthetic.main.activity_password_set.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/25 21:29
 */
class PasswordSetActivity : BaseActivity(), DigitKeyboard.DigitKeyboardClickListener,
    PasswordEditText.OnFinishListener {

    companion object {
        private const val Tag: String = "PasswordSetActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_password_set

    override fun initData() {
        mTitleView.text = "设置密码"
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
        Log.d(Tag, "设置密码: $password")
        if (password != null || password != "") {
            SaveKeyValues.putValue("firstPassword", password)
        }
        //第一遍设置完成后再来设置一次，两次对比相同就保存密码，否则重新对比
        startActivity(Intent(this, PasswordDoubleCheckActivity::class.java))
        finish()
    }

    override fun onPasswordChanged(password: String?) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val isConsumer: Boolean = digitKeyboard.dispatchKeyEventInFullScreen(event)
        return if (isConsumer) isConsumer else super.onKeyDown(keyCode, event)
    }
}