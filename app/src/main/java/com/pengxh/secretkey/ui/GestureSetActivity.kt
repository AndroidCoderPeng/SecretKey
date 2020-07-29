package com.pengxh.secretkey.ui

import android.graphics.Color
import android.os.Handler
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.gesture.GestureLockLayout
import com.pengxh.secretkey.widgets.gesture.GestureLockLayout.OnLockResetListener
import kotlinx.android.synthetic.main.activity_gesture.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/28 22:29
 */
class GestureSetActivity : BaseNormalActivity() {

    override fun initLayoutView(): Int = R.layout.activity_gesture

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "设置手势解锁密码"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        mTitleLeftView.setOnClickListener { this.finish() }

        //设置提示view 每行每列点的个数
        displayView.setDotCount(3)
        //设置提示view 选中状态的颜色
        displayView.setDotSelectedColor(ColorHelper.getXmlColor(this, R.color.colorAccent))
        //设置手势解锁view 每行每列点的个数
        gestureLockLayout.setDotCount(3)
        //设置手势解锁view 最少连接数
        gestureLockLayout.setMinCount(4)
        //默认解锁样式为手Q手势解锁样式
        //mGestureLockLayout.setLockView()
        //设置手势解锁view 模式为重置密码模式
        gestureLockLayout.setMode(GestureLockLayout.RESET_MODE)
        gestureLockLayout.setOnLockResetListener(object : OnLockResetListener {
            override fun onConnectCountUnmatched(connectCount: Int, minCount: Int) {
                //连接数小于最小连接数时调用
                settingHint.text = "最少连接" + minCount + "个点"
                resetGesture()
            }

            override fun onFirstPasswordFinished(answerList: MutableList<Int>) {
                //第一次绘制手势成功时调用
                settingHint.text = "确认解锁图案"
                //将答案设置给提示view
                displayView.setAnswer(answerList)
                //重置
                resetGesture()
            }

            override fun onSetPasswordFinished(isMatched: Boolean, answerList: MutableList<Int>) {
                //第二次密码绘制成功时调用
                if (isMatched) {
                    //两次答案一致，保存
                    EasyToast.showToast("验证通过", EasyToast.SUCCESS)
                } else {
                    resetGesture()
                }
            }
        })
    }

    private val mHandler: Handler = Handler()

    private fun resetGesture() {
        mHandler.postDelayed({ gestureLockLayout.resetGesture() }, 200)
    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}