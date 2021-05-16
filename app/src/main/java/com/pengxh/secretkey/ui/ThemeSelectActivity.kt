package com.pengxh.secretkey.ui

import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.include_title_cyan.*

class ThemeSelectActivity : BaseActivity() {

    companion object {
        private const val Tag = "ThemeSelectActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_theme

    override fun setupTopBarLayout() {

    }

    override fun initData() {
        mTitleView.text = "主题风格"
    }

    override fun initEvent() {

    }
}