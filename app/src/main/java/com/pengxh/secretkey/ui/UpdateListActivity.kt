package com.pengxh.secretkey.ui

import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.app.multilib.utils.StringUtil
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.UpdateViewAdapter
import com.pengxh.secretkey.bean.UpdateLogBean
import kotlinx.android.synthetic.main.activity_update_list.*

class UpdateListActivity : BaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_update_list

    override fun setupTopBarLayout() {
        topLayout.setTitle("更新日志").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {
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