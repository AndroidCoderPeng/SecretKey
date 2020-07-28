package com.pengxh.secretkey.ui

import android.graphics.Color
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.GestureLockView
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/28 22:29
 */
class GestureSetActivity : BaseNormalActivity(), GestureLockView.GestureCallBack {

    override fun initLayoutView(): Int = R.layout.activity_gesture

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "设置手势解锁密码"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        mTitleLeftView.setOnClickListener { this.finish() }
    }

    override fun onVerifySuccessListener(stateFlag: Int,
        data: MutableList<GestureLockView.GestureBean>?,
        success: Boolean) {

    }
}