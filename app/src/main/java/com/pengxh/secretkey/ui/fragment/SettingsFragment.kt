package com.pengxh.secretkey.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.PasswordSetActivity
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.include_title_main.*

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

    private var errorCount = 0

    override fun initLayoutView(): Int = R.layout.fragment_settings

    override fun initData() {
        activity?.let { StatusBarColorUtil.setColor(it, Color.parseColor("#03DAC5")) }
        ImmersionBar.with(this).init()

        mTitleLeftView.visibility = View.GONE
        mTitleView.text = "设置中心"
        mTitleRightView.visibility = View.GONE
    }

    override fun initEvent() {
        if (errorCount < 3) {
            passwordLayout.setOnClickListener {
                //需要判断之前有无设置密码
                val password = SaveKeyValues.getValue("password", "") as String
                Log.d(Tag, "原先设置的密码是: $password")
                if (password == "") {
                    enterPasswordSetActivity()
                } else {
                    //显示密码输入框，验证通过后即可进入密码设置页面
                    if (true) {
                        enterPasswordSetActivity()
                    } else {
                        //提示错误，并且记录错误次数，错误三次就需要等待一小时后再试
                        errorCount++
                        EasyToast.showToast("密码错误，您还可以尝试" + (3 - errorCount) + "次", EasyToast.ERROR)
                    }
                }
            }
        } else {
            EasyToast.showToast("休息1小时后再试吧", EasyToast.WARING)
        }
    }

    /**
     * 密码设置页面
     * */
    private fun enterPasswordSetActivity() {
        startActivity(Intent(context, PasswordSetActivity::class.java))
    }
}