package com.pengxh.secretkey.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.AboutActivity
import com.pengxh.secretkey.ui.InputDataActivity
import com.pengxh.secretkey.ui.PasswordModeActivity
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.ExcelHelper
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.SQLiteUtil
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
                val allSecret = SQLiteUtil().loadAllSecret()

                val mode = SaveKeyValues.getValue(Constant.PASSWORD_MODE, "") as String
                if (mode == "closePassword" || mode == "") {
                    EasyToast.showToast("没有设置启动密码，无法导出", EasyToast.ERROR)
                } else {
                    if (allSecret.size > 0) {
                        //写入到excel
                        Log.d(Tag, "待写入数据: " + Gson().toJson(allSecret))
                        val dir = File(Environment.getExternalStorageDirectory(), "SecretKey")
                        val file = File("$dir/密码管家.xls")
                        if (!file.exists()) {
                            file.createNewFile()
                        }
                        Log.d(Tag, "initEvent: 写入表格-开始")
                        ExcelHelper.initExcel(file, Constant.excelTitle)
                        ExcelHelper.writeSecretToExcel(allSecret)
                        Log.d(Tag, "initEvent: 写入表格-结束")
                        OtherUtils.showAlertDialog(
                            it,
                            "温馨提示",
                            "导出数据到本地成功，文件路径: ${file.absolutePath}"
                        )
                    } else {
                        EasyToast.showToast("没有数据，无法导出", EasyToast.WARING)
                    }
                }
            }
        }

        /**
         * 数据导入Layout
         * */
        inputLayout.setOnClickListener {
            context?.let {
                startActivity(Intent(it, InputDataActivity::class.java))
            }
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
            AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("温馨提示")
                .setMessage("截屏开关切换后需要重启应用才能生效")
                .setCancelable(true)
                .setPositiveButton("知道了", null)
                .create().show()
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
            AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("温馨提示")
                .setMessage("确定清除缓存？")
                .setCancelable(false)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        FileUtil.deleteFile(file)
                        cacheSizeView.text = FileUtil.formatFileSize(0)
                        EasyToast.showToast("清理成功", EasyToast.SUCCESS)
                    }
                }).create().show()
        }
    }
}