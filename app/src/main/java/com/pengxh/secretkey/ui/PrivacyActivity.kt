package com.pengxh.secretkey.ui

import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.include_title_cyan.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 隐私政策
 * @date: 2020/6/22 16:38
 */
class PrivacyActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_privacy

    override fun initData() {
        mTitleView.text = "隐私政策"
    }

    override fun initEvent() {

    }
}