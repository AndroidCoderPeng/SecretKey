package com.pengxh.secretkey.ui

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.GridView
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.KeyBoardAdapter
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_setpassword.*
import kotlinx.android.synthetic.main.include_title_white.*
import me.shaohui.bottomdialog.BottomDialog

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/25 21:29
 */
class PasswordSetActivity : BaseNormalActivity() {

    companion object {
        private const val Tag: String = "PasswordSetActivity"
        private val keyBoardArray =
            arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "X")
    }
    override fun initLayoutView(): Int = R.layout.activity_setpassword

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "设置密码"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        mTitleLeftView.setOnClickListener { this.finish() }

        passwordLayout.setOnClickListener {
            BottomDialog.create(supportFragmentManager).setLayoutRes(R.layout.bottom_sheet)
                .setViewListener {
                    val keyboardView = it.findViewById(R.id.keyboardView) as GridView
                    keyboardView.adapter = KeyBoardAdapter(this, keyBoardArray)
                    keyboardView.setOnItemClickListener { parent, view, position, id ->
                        Log.d(Tag, ": " + keyBoardArray[position])
                    }
                }.setDimAmount(0.0f).setCancelOutside(true).show()
        }
    }
}