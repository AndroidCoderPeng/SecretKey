package com.pengxh.secretkey.ui

import android.graphics.Color
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 隐私政策
 * @date: 2020/6/22 16:38
 */
class PrivacyActivity : BaseNormalActivity() {

    override fun initLayoutView(): Int = R.layout.activity_privacy

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "隐私政策"
    }

    override fun initEvent() {

    }
}