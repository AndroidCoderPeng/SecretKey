package com.pengxh.secretkey.ui

import androidx.core.content.ContextCompat
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.BuildConfig
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.OtherUtils
import kotlinx.android.synthetic.main.activity_about.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/30 16:56
 */
class AboutActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_about

    override fun setupTopBarLayout() {
        topLayout.setTitle("关于应用").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {

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