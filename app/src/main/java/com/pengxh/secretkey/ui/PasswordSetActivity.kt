package com.pengxh.secretkey.ui

import android.graphics.Color
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/25 21:29
 */
class PasswordSetActivity : BaseNormalActivity() {

    override fun initLayoutView(): Int = R.layout.activity_setpassword

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "设置密码"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        mTitleLeftView.setOnClickListener { this.finish() }
    }
}