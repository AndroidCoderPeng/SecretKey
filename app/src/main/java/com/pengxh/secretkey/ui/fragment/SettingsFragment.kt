package com.pengxh.secretkey.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.GestureCheckActivity
import com.pengxh.secretkey.ui.GestureSetActivity
import com.pengxh.secretkey.ui.PasswordCheckActivity
import com.pengxh.secretkey.ui.PasswordSetActivity
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.include_title_main.*
import java.io.File

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020年7月24日12:41:47
 */
class SettingsFragment : BaseFragment() {

    companion object {
        private const val Tag = "SettingsFragment"
    }

    override fun initLayoutView(): Int = R.layout.fragment_settings

    override fun initData() {
        activity?.let { StatusBarColorUtil.setColor(it, Color.parseColor("#03DAC5")) }
        ImmersionBar.with(this).init()

        mTitleLeftView.visibility = View.GONE
        mTitleView.text = "设置中心"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        /**
         * 密码设置Layout
         * */
        passwordLayout.setOnClickListener {
            //判断解密模式
            when (SaveKeyValues.getValue("mode", "numberSwitch") as String) {
                "numberSwitch" -> {
                    //需要判断之前有无设置密码
                    val firstPassword = SaveKeyValues.getValue("firstPassword", "") as String
                    if (firstPassword == "") {
                        startActivity(Intent(context, PasswordSetActivity::class.java))
                    } else {
                        startActivity(Intent(context, PasswordCheckActivity::class.java))
                    }
                }
                "gestureSwitch" -> {
                    val gesturePassword = SaveKeyValues.getValue("gesturePassword", "") as String
                    Log.d(Tag, "gestureSwitch: $gesturePassword")
                    if (gesturePassword == "") {
                        startActivity(Intent(context, GestureSetActivity::class.java))
                    } else {
                        startActivity(Intent(context, GestureCheckActivity::class.java))
                    }
                }
                "fingerprintSwitch" -> {
                    EasyToast.showToast("指纹验证", EasyToast.DEFAULT)
                }
            }
        }

        /**
         * 清除缓存Layout
         * */
        val file = File(context!!.cacheDir.absolutePath)
        val fileSize = FileUtil.getFileSize(file)
        Log.d(Tag, ": $fileSize")
        cacheSizeView.text = FileUtil.formatFileSize(fileSize)
        cacheSizeLayout.setOnClickListener {
            AlertView("温馨提示",
                "确定清除缓存？",
                "取消",
                arrayOf("确定"),
                null,
                context,
                AlertView.Style.Alert,
                OnItemClickListener { o, position ->
                    if (position == 0) {
                        FileUtil.deleteFile(file)
                        cacheSizeView.text = FileUtil.formatFileSize(0)
                        EasyToast.showToast("清理成功", EasyToast.SUCCESS)
                    }
                }).setCancelable(false).show()
        }
    }
}