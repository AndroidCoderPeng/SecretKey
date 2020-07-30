package com.pengxh.secretkey.ui

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.ViewPagerAdapter
import com.pengxh.secretkey.ui.fragment.HomePageFragment
import com.pengxh.secretkey.ui.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var menuItem: MenuItem? = null
    private lateinit var fragmentList: ArrayList<Fragment>

    override fun initLayoutView(): Int = R.layout.activity_main

    override fun initData() {
        fragmentList = ArrayList()
        fragmentList.add(HomePageFragment())
        fragmentList.add(SettingsFragment())
    }

    override fun initEvent() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> mainViewPager.currentItem = 0
                R.id.nav_settings -> mainViewPager.currentItem = 1
            }
            false
        }
        mainViewPager.adapter = ViewPagerAdapter(fragmentList, supportFragmentManager)
        mainViewPager.offscreenPageLimit = fragmentList.size //缓存页数
        mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (menuItem != null) {
                    menuItem!!.isChecked = false
                } else {
                    bottomNavigation.menu.getItem(0).isChecked = false
                }
                menuItem = bottomNavigation.menu.getItem(position)
                menuItem!!.isChecked = true
            }
        })
    }
}
