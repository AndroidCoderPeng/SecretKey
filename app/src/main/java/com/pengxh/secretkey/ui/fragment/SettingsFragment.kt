package com.pengxh.secretkey.ui.fragment

import android.util.Log
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.AboutActivity
import com.pengxh.secretkey.ui.PasswordModeActivity
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File
import kotlin.properties.Delegates


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

    private var captureSwitchStatus by Delegates.notNull<Boolean>()

    override fun initLayoutView(): Int = R.layout.fragment_settings

    override fun initData() {
        activity?.let {
            StatusBarColorUtil.setColor(it, ColorHelper.getXmlColor(it, R.color.colorAccent))
        }
        ImmersionBar.with(this).init()

        settingsTitle.text = "设置中心"

        //默认不让截屏
        captureSwitchStatus = SaveKeyValues.getValue("captureSwitchStatus", false) as Boolean
        captureSwitch.isChecked = captureSwitchStatus
    }

    override fun initEvent() {
        /**
         * 密码设置Layout
         * */
        passwordLayout.setOnClickListener {
            OtherUtils.intentActivity(PasswordModeActivity::class.java)
        }

        /**
         * 关于Layout
         * */
        aboutLayout.setOnClickListener {
            OtherUtils.intentActivity(AboutActivity::class.java)
        }

        /**
         * 截屏开关Layout
         * */
        captureLayout.setOnClickListener {
            AlertView("温馨提示",
                "截屏开关切换后需要重启应用才能生效",
                null,
                arrayOf("知道了"),
                null,
                context,
                AlertView.Style.Alert,
                null).setCancelable(false).show()
        }
        /**
         * 截屏开关状态管理
         * */
        captureSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SaveKeyValues.putValue("captureSwitchStatus", true)
            } else {
                SaveKeyValues.putValue("captureSwitchStatus", false)
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