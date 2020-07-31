package com.pengxh.secretkey.ui.fragment

import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretCategoryAdapter
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020年7月24日12:41:36
 */
class HomePageFragment : BaseFragment() {

    companion object {
        private const val Tag = "HomePageFragment"
    }

    override fun initLayoutView(): Int = R.layout.fragment_home

    override fun initData() {
        activity?.let {
            StatusBarColorUtil.setColor(it, ColorHelper.getXmlColor(it, R.color.colorAccent))
        }
        ImmersionBar.with(this).init()
    }

    override fun initEvent() {
        secretGridView.adapter = context?.let { SecretCategoryAdapter(it) }
    }
}