package com.pengxh.secretkey.ui

import android.graphics.Color
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.BuildConfig
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/30 16:56
 */
class AboutActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_about

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "关于应用"
    }

    override fun initEvent() {
        versionView.text = BuildConfig.VERSION_NAME

        exonerationLayout.setOnClickListener {
            OtherUtils.intentActivity(ExonerationActivity::class.java)
        }

        privacyLayout.setOnClickListener {
            OtherUtils.intentActivity(PrivacyActivity::class.java)
        }

        updateLayout.setOnClickListener {
            OtherUtils.intentActivity(UpdateListActivity::class.java)
        }
    }
}