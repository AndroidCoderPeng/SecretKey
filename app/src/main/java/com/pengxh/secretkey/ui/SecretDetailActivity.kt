package com.pengxh.secretkey.ui

import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretBean
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

    private lateinit var sqLiteUtil: SQLiteUtil
    private lateinit var secretList: List<SecretBean>

    override fun initLayoutView(): Int = R.layout.activity_secret_detail

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        val category = intent.getStringExtra("mode")
        mTitleView.text = category

        sqLiteUtil = SQLiteUtil(this)
        secretList = sqLiteUtil.loadCategory(category!!)
    }

    override fun initEvent() {
        val secretDetailAdapter = SecretDetailAdapter(this, secretList)
        secretRecyclerView.layoutManager = LinearLayoutManager(this)
        secretRecyclerView.adapter = secretDetailAdapter
        secretDetailAdapter.setOnItemClickListener(object :
            SecretDetailAdapter.OnChildViewClickListener {
            override fun onShareViewClickListener(position: Int) {
                EasyToast.showToast("分享", EasyToast.DEFAULT)
            }

            override fun onCopyViewClickListener(position: Int) {
                EasyToast.showToast("复制", EasyToast.SUCCESS)
            }

            override fun onDeleteViewClickListener(position: Int) {
                EasyToast.showToast("删除", EasyToast.WARING)
            }

            override fun onVisibleViewClickListener(position: Int) {
                //                secretPassword.inputType = InputType.TYPE_CLASS_TEXT
                EasyToast.showToast("删除", EasyToast.ERROR)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(Tag, "返回键: ")
    }
}