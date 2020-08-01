package com.pengxh.secretkey.ui

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_secret_add.*
import kotlinx.android.synthetic.main.include_title_white.*


/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/1 22:53
 */
class AddSecretActivity : BaseActivity() {

    companion object {
        private const val Tag: String = "AddSecretActivity"
    }

    private lateinit var category: String
    private lateinit var title: String
    private lateinit var account: String
    private lateinit var password: String

    override fun initLayoutView(): Int = R.layout.activity_secret_add

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "添加密码"
        mTitleRightView.visibility = View.VISIBLE
        mTitleRightView.setBackgroundResource(R.mipmap.all_right)
    }

    override fun initEvent() {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, Constant.title)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        categorySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                category = Constant.title[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        mTitleRightView.setOnClickListener {
            //将数据存数据库，然后结束当前页面
            this.finish()
        }
    }
}