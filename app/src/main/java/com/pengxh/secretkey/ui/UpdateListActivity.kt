package com.pengxh.secretkey.ui

import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.app.multilib.utils.StringUtil
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.UpdateViewAdapter
import com.pengxh.secretkey.bean.UpdateLogBean
import kotlinx.android.synthetic.main.activity_update_list.*
import kotlinx.android.synthetic.main.include_title_cyan.*

class UpdateListActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_update_list

    override fun initData() {
        mTitleView.text = "更新日志"

        val assetsData = StringUtil.getAssetsData(this, "update.json")
        val type = object : TypeToken<ArrayList<UpdateLogBean>>() {}.type
        val data: ArrayList<UpdateLogBean> = Gson().fromJson(assetsData, type)

        val updateViewAdapter = UpdateViewAdapter(this, data)
        updateListView.adapter = updateViewAdapter
        updateListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, UpdateLogActivity::class.java)
            intent.putExtra("message", data[position].message)
            startActivity(intent)
        }
    }

    override fun initEvent() {

    }
}