package com.pengxh.secretkey.ui

import android.graphics.Color
import android.os.Handler
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.gesture.GestureLockLayout
import com.pengxh.secretkey.widgets.gesture.GestureLockLayout.OnLockVerifyListener
import kotlinx.android.synthetic.main.activity_gesture_set.*
import kotlinx.android.synthetic.main.include_title_white.*


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/29 14:58
 */
class GestureCheckActivity : BaseNormalActivity() {

    override fun initLayoutView(): Int = R.layout.activity_gesture_check

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleLeftView.visibility = View.GONE
        mTitleView.text = "手势解锁"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        //设置手势解锁view 每行每列点的个数
        gestureLockLayout.setDotCount(3)
        //设置手势解锁view 最少连接数
        gestureLockLayout.setMinCount(4)
        //设置手势解锁view 模式为重置密码模式
        gestureLockLayout.setMode(GestureLockLayout.VERIFY_MODE)
        //设置手势解锁最大尝试次数 默认 3
        gestureLockLayout.tryTimes = 3
        //设置手势解锁正确答案
        val gesturePassword = SaveKeyValues.getValue("gesturePassword", "") as String
        gestureLockLayout.setAnswer(gesturePassword)
        gestureLockLayout.setOnLockVerifyListener(object : OnLockVerifyListener {
            override fun onGestureSelected(id: Int) {
                //每选中一个点时调用
            }

            override fun onGestureFinished(isMatched: Boolean) {
                //绘制手势解锁完成时调用
                if (isMatched) {
                    //密码匹配
                    OtherUtils.intentActivity(MainActivity::class.java)
                    finish()
                } else {
                    //不匹配
                    settingHint.text = "还有" + gestureLockLayout.tryTimes.toString() + "次机会"
                    resetGesture()
                }
            }

            override fun onGestureTryTimesBoundary() {
                //超出最大尝试次数时调用
                gestureLockLayout.setTouchable(false)
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