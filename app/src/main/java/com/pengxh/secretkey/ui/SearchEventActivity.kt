package com.pengxh.secretkey.ui

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_secret_detail.emptyLayout
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/5 10:15
 */
class SearchEventActivity : BaseActivity() {

    companion object {
        private const val Tag = "SearchEventActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_search

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "搜索结果"
    }

    override fun initEvent() {
        val keyWords = intent.getStringExtra("mode")
        val beanList = SQLiteUtil(this).loadAccountSecret(keyWords!!)
        Log.d(Tag, Gson().toJson(beanList))
        val secretDetailAdapter = SecretDetailAdapter(this, beanList)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.adapter = secretDetailAdapter
        initUI(beanList)
    }

    private fun initUI(list: MutableList<SecretBean>) {
        if (list.size > 0) {
            searchRecyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.VISIBLE
            searchRecyclerView.visibility = View.GONE
        }
    }
}