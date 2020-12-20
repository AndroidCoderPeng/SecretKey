package com.pengxh.secretkey

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.StatusBarColorUtil

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/30 19:18
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val captureSwitchStatus = SaveKeyValues.getValue("captureSwitchStatus", false) as Boolean
        if (!captureSwitchStatus) {
            //禁止截屏
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        setContentView(initLayoutView())
        StatusBarColorUtil.setColor(this, ColorHelper.getXmlColor(this, R.color.colorAccent))
        ImmersionBar.with(this).init()
        initData()
        initEvent()
    }

    /**
     * 初始化xml布局
     */
    abstract fun initLayoutView(): Int

    /**
     * 初始化默认数据
     */
    abstract fun initData()

    /**
     * 初始化业务逻辑
     */
    abstract fun initEvent()
}