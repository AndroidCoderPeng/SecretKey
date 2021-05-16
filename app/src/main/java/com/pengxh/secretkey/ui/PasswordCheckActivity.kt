package com.pengxh.secretkey.ui

import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.CameraPreviewHelper
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.ToastHelper
import com.pengxh.secretkey.widgets.DigitKeyboard
import com.pengxh.secretkey.widgets.PasswordEditText
import kotlinx.android.synthetic.main.activity_password_check.*
import kotlinx.android.synthetic.main.activity_password_check.topLayout
import kotlinx.android.synthetic.main.activity_password_set.*
import kotlinx.android.synthetic.main.activity_password_set.digitKeyboard
import kotlinx.android.synthetic.main.activity_password_set.passwordEditText

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/28 11:01
 */
class PasswordCheckActivity : BaseActivity(), DigitKeyboard.DigitKeyboardClickListener,
    PasswordEditText.OnFinishListener, CameraPreviewHelper.OnCaptureImageCallback {

    private lateinit var cameraPreviewHelper: CameraPreviewHelper

    override fun initLayoutView(): Int = R.layout.activity_password_check

    override fun setupTopBarLayout() {
        topLayout.setTitle("输入密码").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {

    }

    override fun initEvent() {
        digitKeyboard.setOnDigitKeyboardClickListener(this)
        passwordEditText.setOnFinishListener(this)

        passwordEditText.isEnabled = true
        passwordEditText.isFocusable = false
        passwordEditText.isFocusableInTouchMode = false
        passwordEditText.setOnClickListener { digitKeyboard.visibility = View.VISIBLE }
    }

    override fun onResume() {
        super.onResume()
        cameraPreviewHelper =
            CameraPreviewHelper(this, monitorView, "1", CameraCharacteristics.LENS_FACING_FRONT)
        cameraPreviewHelper.setImageCallback(this)
    }

    override fun onClick(number: String?) {
        if (number != null) {
            passwordEditText.addPassword(number)
        }
    }

    override fun onDelete() {
        passwordEditText.deleteLastPassword()
    }

    override fun onPasswordFinish(password: String?) {
        if (password != null || password != "") {
            val firstPassword = SaveKeyValues.getValue("firstPassword", "") as String
            if (password == firstPassword) {
                OtherUtils.intentActivity(MainActivity::class.java)
                finish()
            } else {
//                cameraPreviewHelper.takePicture()
                ToastHelper.showToast("密码错误，请重试", ToastHelper.ERROR)
            }
        }
    }


    override fun captureImage(localPath: String?, bitmap: Bitmap?) {
        Log.d("PasswordCheckActivity", ": $localPath")
    }

    override fun onPasswordChanged(password: String?) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val isConsumer: Boolean = digitKeyboard.dispatchKeyEventInFullScreen(event)
        return if (isConsumer) isConsumer else super.onKeyDown(keyCode, event)
    }
}