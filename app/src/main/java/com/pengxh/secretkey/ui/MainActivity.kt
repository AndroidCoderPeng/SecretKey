package com.pengxh.secretkey.ui

import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.ViewPagerAdapter
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.ui.fragment.HomePageFragment
import com.pengxh.secretkey.ui.fragment.SecretListFragment
import com.pengxh.secretkey.ui.fragment.SettingsFragment
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.ExcelHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.ref.WeakReference

class MainActivity : BaseActivity() {

    companion object {
        private const val Tag = "MainActivity"

        private lateinit var weakReferenceHandler: WeakReferenceHandler

        fun sendEmptyMessage(what: Int) {
            weakReferenceHandler.sendEmptyMessage(what)
        }
    }

    private var menuItem: MenuItem? = null
    private var fragmentList: ArrayList<Fragment>

    init {
        weakReferenceHandler = WeakReferenceHandler(this)

        fragmentList = ArrayList()
        fragmentList.add(HomePageFragment())
        fragmentList.add(SecretListFragment())
        fragmentList.add(SettingsFragment())
    }

    private class WeakReferenceHandler(activity: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()
            when (msg.what) {
                Constant.FINISH_APP -> {
                    activity?.finish()
                }
            }
        }
    }

    override fun initLayoutView(): Int = R.layout.activity_main

    override fun setupTopBarLayout() {
        topLayout.setTitle("密码箱").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {
        //初始化数据文件夹和文件
        val dir =
            File(Environment.getExternalStorageDirectory(), "SecretKey")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir.toString() + File.separator + "密码管家模板表格.xls")
        if (!file.exists()) {
            file.createNewFile()
        }
        //写入模板数据
        ExcelHelper.initExcel(file, Constant.excelTitle)
        //模拟数据
        val secretData: MutableList<SecretSQLiteBean> = ArrayList()
        val demoBean = SecretSQLiteBean()
        demoBean.category = "网站"
        demoBean.title = "淘宝网"
        demoBean.account = "ABC"
        demoBean.password = "123456789"
        demoBean.remarks = "这里是个选填项"
        secretData.add(demoBean)
        ExcelHelper.writeSecretToExcel(secretData)
    }

    override fun initEvent() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    mainViewPager.currentItem = 0
                    topLayout.setTitle("密码箱")
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                R.id.nav_secret -> {
                    mainViewPager.currentItem = 1
                    topLayout.setTitle("密码列表")
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                R.id.nav_settings -> {
                    mainViewPager.currentItem = 2
                    topLayout.setTitle("设置中心")
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                }
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
    }
}
