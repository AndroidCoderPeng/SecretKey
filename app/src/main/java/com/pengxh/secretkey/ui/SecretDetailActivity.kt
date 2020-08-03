package com.pengxh.secretkey.ui

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_secret_detail.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/3 10:48
 */
class SecretDetailActivity : BaseActivity() {

    companion object {
        private const val Tag = "SecretDetailActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_secret_detail

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        val category = intent.getStringExtra("mode")
        mTitleView.text = category

        val secretList = SQLiteUtil(this).loadCategory(category!!)
        secretRecyclerView.layoutManager = LinearLayoutManager(this)
        secretRecyclerView.adapter = SecretDetailAdapter(this, secretList)
    }

    override fun initEvent() {

    }
}