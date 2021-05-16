package com.pengxh.secretkey.ui

import androidx.core.content.ContextCompat
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.activity_exoneration.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 隐私政策
 * @date: 2020/6/22 16:38
 */
class PrivacyActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_privacy

    override fun setupTopBarLayout() {
        topLayout.setTitle("隐私政策").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {

    }

    override fun initEvent() {

    }
}