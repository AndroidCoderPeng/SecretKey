package com.pengxh.secretkey.ui

import androidx.core.content.ContextCompat
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.activity_exoneration.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/6/22 16:37
 */
class ExonerationActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_exoneration

    override fun setupTopBarLayout() {
        topLayout.setTitle("免责声明").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {

    }

    override fun initEvent() {

    }
}