package com.pengxh.secretkey.ui

import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.include_title_cyan.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 免责声明
 * @date: 2020/6/22 16:37
 */
class ExonerationActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_exoneration

    override fun initData() {
        mTitleView.text = "免责声明"
    }

    override fun initEvent() {

    }
}