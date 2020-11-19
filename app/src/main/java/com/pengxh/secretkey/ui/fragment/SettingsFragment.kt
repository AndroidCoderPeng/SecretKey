package com.pengxh.secretkey.ui.fragment

import android.os.Environment
import android.util.Log
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.AboutActivity
import com.pengxh.secretkey.ui.PasswordModeActivity
import com.pengxh.secretkey.utils.*
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
        private val excelTitle = arrayOf("类别", "标题", "账号", "密码", "备注")
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
         * 数据导出Layout
         * */
        outputLayout.setOnClickListener {
            context?.let {
                val allSecret = SQLiteUtil(it).loadAllSecret()

                //写入到excel
                Log.d(Tag, Gson().toJson(allSecret))
                val dir =
                    File(Environment.getExternalStorageDirectory(), "SecretKey")
                if (!dir.exists()) {
                    dir.mkdir()
                }
                Log.d(Tag, "initEvent: 写入表格-开始")
                ExcelHelper.initExcel("$dir/密码管家数据.xls", excelTitle)
                ExcelHelper.writeSecretToExcel(allSecret)
                Log.d(Tag, "initEvent: 写入表格-结束")
            }
        }

        /**
         * 数据导入Layout
         * */
        inputLayout.setOnClickListener {

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
         * 截屏状态开关
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