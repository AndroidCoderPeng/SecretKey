package com.pengxh.secretkey.ui

import android.graphics.Color
import android.util.Log
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/4 15:43
 */
class RecoverableActivity : BaseActivity() {

    companion object {
        private const val Tag = "RecoverableActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_recoverable

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "数据恢复"
    }

    override fun initEvent() {
        Log.d(Tag, Gson().toJson(SQLiteUtil(this).loadRecoverableData()))
    }
}