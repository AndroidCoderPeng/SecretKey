package com.pengxh.secretkey.ui.fragment

import android.util.Log
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.AboutActivity
import com.pengxh.secretkey.ui.PasswordModeActivity
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.OtherUtils
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
        activity?.let {
            StatusBarColorUtil.setColor(it, ColorHelper.getXmlColor(it, R.color.colorAccent))
        }
        ImmersionBar.with(this).init()

        mTitleView.text = "设置中心"
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