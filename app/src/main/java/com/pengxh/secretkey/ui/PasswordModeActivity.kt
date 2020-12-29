package com.pengxh.secretkey.ui

import android.content.Intent
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.OtherUtils
import kotlinx.android.synthetic.main.activity_password_mode.*
import kotlinx.android.synthetic.main.include_title_cyan.*
import kotlin.properties.Delegates

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/28 11:04
 */
class PasswordModeActivity : BaseActivity() {

    private var captureSwitchStatus by Delegates.notNull<Boolean>()

    override fun initLayoutView(): Int = R.layout.activity_password_mode

    override fun initData() {
        mTitleView.text = "设置解锁方式"


        //默认不让截屏
        captureSwitchStatus = SaveKeyValues.getValue("captureSwitchStatus", false) as Boolean
        captureSwitch.isChecked = captureSwitchStatus
    }

    /**
     * 解锁方式切换，事件相互独立，有且只能有一种解锁方式
     * */
    override fun initEvent() {
        /**
         * 截屏状态开关
         * */
        captureSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SaveKeyValues.putValue("captureSwitchStatus", true)
            } else {
                SaveKeyValues.putValue("captureSwitchStatus", false)
            }
            startActivity(Intent(this, EmptyActivity::class.java))
        }

        numberSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                gestureSwitch.isChecked = false
                fingerprintSwitch.isChecked = false
                closePasswordSwitch.isChecked = false
                SaveKeyValues.putValue(Constant.PASSWORD_MODE, "numberSwitch")
            }
        }

        gestureSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            val gesturePassword = SaveKeyValues.getValue("gesturePassword", "") as String
            if (gesturePassword == "") {
                buttonView.isChecked = false
                startActivity(Intent(this, GestureSetActivity::class.java))
            } else {
                if (isChecked) {
                    numberSwitch.isChecked = false
                    fingerprintSwitch.isChecked = false
                    closePasswordSwitch.isChecked = false
                    SaveKeyValues.putValue(Constant.PASSWORD_MODE, "gestureSwitch")
                }
            }
        }

        fingerprintSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!OtherUtils.isSupportFingerprint()) {
                buttonView.isChecked = false
                EasyToast.showToast("设备不支持指纹识别或者未录入指纹", EasyToast.ERROR)
            } else {
                if (isChecked) {
                    numberSwitch.isChecked = false
                    gestureSwitch.isChecked = false
                    closePasswordSwitch.isChecked = false
                    SaveKeyValues.putValue(Constant.PASSWORD_MODE, "fingerprintSwitch")
                }
            }
        }

        closePasswordSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                numberSwitch.isChecked = false
                gestureSwitch.isChecked = false
                fingerprintSwitch.isChecked = false
                SaveKeyValues.putValue(Constant.PASSWORD_MODE, "closePassword")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (SaveKeyValues.getValue(Constant.PASSWORD_MODE, "numberSwitch") as String) {
            "numberSwitch" -> {
                numberSwitch.isChecked = true
                gestureSwitch.isChecked = false
                fingerprintSwitch.isChecked = false
                closePasswordSwitch.isChecked = false
            }
            "gestureSwitch" -> {
                numberSwitch.isChecked = false
                gestureSwitch.isChecked = true
                fingerprintSwitch.isChecked = false
                closePasswordSwitch.isChecked = false
            }
            "fingerprintSwitch" -> {
                numberSwitch.isChecked = false
                gestureSwitch.isChecked = false
                fingerprintSwitch.isChecked = true
                closePasswordSwitch.isChecked = false
            }
            "closePassword" -> {
                numberSwitch.isChecked = false
                gestureSwitch.isChecked = false
                fingerprintSwitch.isChecked = false
                closePasswordSwitch.isChecked = true
            }
        }
    }
}