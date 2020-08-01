package com.pengxh.secretkey.ui.fragment

import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.secretkey.R

/**
 * @description: TODO 占位页，实现底部凸起导航的一种投机取巧的方式，少这个页面会导致底部导航栏颜色错位
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/1 22:37
 */
class EmptyFragment : BaseFragment() {

    override fun initLayoutView(): Int = R.layout.fragment_empty

    override fun initData() {
    }

    override fun initEvent() {
    }
}