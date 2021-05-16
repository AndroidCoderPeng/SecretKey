package com.pengxh.secretkey.ui

import androidx.core.content.ContextCompat
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.activity_update_log.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/8 17:10
 */
class UpdateLogActivity : BaseActivity() {

    private lateinit var updateMessage: String

    override fun initLayoutView(): Int = R.layout.activity_update_log

    override fun setupTopBarLayout() {
        topLayout.setTitle("更新详情").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {
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
            updateMessage.contains("密码管家2.2版本主要更新") -> {
                staticURL = "file:///android_asset/html/version_2_2.html"
            }
            updateMessage.contains("密码管家2.3版本主要更新") -> {
                staticURL = "file:///android_asset/html/version_2_3.html"
            }
        }
        updateWebView.settings.defaultTextEncodingName = "utf-8"
        updateWebView.loadUrl(staticURL)
    }
}