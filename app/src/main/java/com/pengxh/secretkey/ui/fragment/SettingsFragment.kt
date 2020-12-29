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
import com.pengxh.secretkey.ui.ThemeSelectActivity
import com.pengxh.secretkey.utils.*
import com.pengxh.secretkey.widgets.InputDialog
import kotlinx.android.synthetic.main.fragment_settings.*
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

    private val dir =
        File(Environment.getExternalStorageDirectory(), "SecretKey").toString() + File.separator
    private lateinit var path: String

    override fun initLayoutView(): Int = R.layout.fragment_settings

    override fun initData() {
        settingsTitle.text = "设置中心"
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
                        InputDialog.Builder()
                            .setContext(context)
                            .setDialogTitle("导出到表格")
                            .setDialogMessage("请输入文件名")
                            .setOnDialogClickListener(object : InputDialog.DialogClickListener {
                                override fun onConfirmClicked(input: String) {
                                    path = if (input == "") {
                                        dir + "密码管家_" + StringHelper.formatDate() + ".xls"
                                    } else {
                                        "$dir$input.xls"
                                    }
                                    val file = File(path)
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
                                }
                            }).build().show(fragmentManager!!, "outputData")
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
         * 主题选择Layout
         * */
        themeLayout.setOnClickListener {
            OtherUtils.intentActivity(ThemeSelectActivity::class.java)
        }

        /**
         * 关于Layout
         * */
        aboutLayout.setOnClickListener {
            OtherUtils.intentActivity(AboutActivity::class.java)
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