package com.pengxh.secretkey.ui

import android.graphics.Color
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_update_log.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/8 17:10
 */
class UpdateLogActivity : BaseActivity() {

    private lateinit var updateMessage: String

    override fun initLayoutView(): Int = R.layout.activity_update_log

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "更新详情"

        updateMessage = intent.getStringExtra("message")!!
    }

    override fun initEvent() {
        var staticURL = ""
        when {
            updateMessage.contains("密码管家立项") -> {
                staticURL = "file:///android_asset/html/normal.html"
            }
            updateMessage.contains("密码管家1.0版本主要更新") -> {
                staticURL = "file:///android_asset/html/version_1.html"
            }
            updateMessage.contains("密码管家1.1版本主要更新") -> {
                staticURL = "file:///android_asset/html/version_1_1.html"
            }
            updateMessage.contains("密码管家2.0版本主要更新") -> {
                staticURL = "file:///android_asset/html/version_2.html"
            }
            updateMessage.contains("密码管家2.1版本主要更新") -> {
                staticURL = "file:///android_asset/html/version_2_1.html"
            }
        }
        updateWebView.settings.defaultTextEncodingName = "utf-8"
        updateWebView.loadUrl(staticURL)
    }
}