package com.pengxh.secretkey.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Environment
import android.text.InputType
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.secretkey.BaseApplication
import com.pengxh.secretkey.R
import com.pengxh.secretkey.greendao.SecretSQLiteBeanDao
import com.pengxh.secretkey.ui.AboutActivity
import com.pengxh.secretkey.ui.InputDataActivity
import com.pengxh.secretkey.ui.PasswordModeActivity
import com.pengxh.secretkey.utils.*
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.EditTextDialogBuilder
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020年7月24日12:41:47
 */
class SettingsFragment : BaseFragment() {

    private var dir: String =
        File(Environment.getExternalStorageDirectory(), "SecretKey").toString() + File.separator
    private var secretBeanDao: SecretSQLiteBeanDao =
        BaseApplication.instance().obtainDaoSession().secretSQLiteBeanDao
    private lateinit var path: String

    override fun initLayoutView(): Int = R.layout.fragment_settings

    override fun initData() {

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
                val allSecret = secretBeanDao.loadAll()

                val mode = SaveKeyValues.getValue(Constant.PASSWORD_MODE, "") as String
                if (mode == "closePassword" || mode == "") {
                    ToastHelper.showToast("没有设置启动密码，无法导出", ToastHelper.ERROR)
                } else {
                    if (allSecret.size > 0) {
                        //写入到excel
                        val builder = EditTextDialogBuilder(context)
                        builder.setTitle("导出到表格")
                            .setPlaceholder("请输入文件名")
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .setCancelable(false)
                            .addAction("取消") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .addAction("确定") { dialog, _ ->
                                dialog.dismiss()
                                val input: String = builder.editText.text.toString()
                                path = if (input == "") {
                                    dir + "密码管家_" + StringHelper.formatDate() + ".xls"
                                } else {
                                    "$dir$input.xls"
                                }
                                val file = File(path)
                                if (!file.exists()) {
                                    file.createNewFile()
                                }
                                ExcelHelper.initExcel(file, Constant.excelTitle)
                                ExcelHelper.writeSecretToExcel(allSecret)
                                OtherUtils.showAlertDialog(
                                    it,
                                    "温馨提示",
                                    "导出数据到本地成功，文件路径: ${file.absolutePath}"
                                )
                            }
                            .show()
                    } else {
                        ToastHelper.showToast("没有数据，无法导出", ToastHelper.WARING)
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
                        ToastHelper.showToast("清理成功", ToastHelper.SUCCESS)
                    }
                }).create().show()
        }
    }
}