package com.pengxh.secretkey.ui

import android.os.Environment
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.ViewPagerAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.ui.fragment.HomePageFragment
import com.pengxh.secretkey.ui.fragment.SecretListFragment
import com.pengxh.secretkey.ui.fragment.SettingsFragment
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.ExcelHelper
import com.pengxh.secretkey.utils.OtherUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() {

    private var menuItem: MenuItem? = null
    private lateinit var fragmentList: ArrayList<Fragment>

    override fun initLayoutView(): Int = R.layout.activity_main

    override fun initData() {
        fragmentList = ArrayList()
        fragmentList.add(HomePageFragment())
        fragmentList.add(SecretListFragment())
        fragmentList.add(SettingsFragment())

        //初始化数据文件夹和文件
        initExcelDemo()
    }

    private fun initExcelDemo() {
        val dir =
            File(Environment.getExternalStorageDirectory(), "SecretKey")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File("$dir/密码管家模板表格.xls")
        if (!file.exists()) {
            file.createNewFile()
        }
        //写入模板数据
        ExcelHelper.initExcel(file, Constant.excelTitle)
        //模拟数据
        val secretData: MutableList<SecretBean> = java.util.ArrayList()
        val demoBean = SecretBean()
        demoBean.secretCategory = "网站"
        demoBean.secretTitle = "淘宝网"
        demoBean.secretAccount = "ABC"
        demoBean.secretPassword = "123456789"
        demoBean.secretRemarks = "这里是个选填项"
        secretData.add(demoBean)
        ExcelHelper.writeSecretToExcel(secretData)
    }

    override fun initEvent() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> mainViewPager.currentItem = 0
                R.id.nav_secret -> mainViewPager.currentItem = 1
                R.id.nav_settings -> mainViewPager.currentItem = 2
            }
            false
        }
        mainViewPager.adapter = ViewPagerAdapter(fragmentList, supportFragmentManager)
        mainViewPager.offscreenPageLimit = fragmentList.size //缓存页数
        mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

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

        outlineFab.setOnClickListener {
            OtherUtils.intentActivity(AddSecretActivity::class.java)
        }
    }
}
