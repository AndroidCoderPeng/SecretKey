package com.pengxh.secretkey.ui

import android.graphics.Color
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/8 17:10
 */
class UpdateLogActivity : BaseActivity() {


    override fun initLayoutView(): Int = R.layout.activity_update_log

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()
    }

    override fun initEvent() {

    }
}