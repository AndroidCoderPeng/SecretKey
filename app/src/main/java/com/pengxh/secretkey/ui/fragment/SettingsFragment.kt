package com.pengxh.secretkey.ui.fragment

import android.graphics.Color
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.include_title.*

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

    }
}