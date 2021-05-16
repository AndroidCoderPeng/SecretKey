package com.pengxh.secretkey

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.qmuiteam.qmui.util.QMUIStatusBarHelper

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/30 19:18
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val Tag = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayoutView())
        QMUIStatusBarHelper.translucent(this)//沉浸式状态栏
        setupTopBarLayout()
        initData()
        initEvent()
    }

    override fun onStart() {
        super.onStart()
        val captureSwitchStatus = SaveKeyValues.getValue("captureSwitchStatus", false) as Boolean
        if (!captureSwitchStatus) {
            //禁止截屏
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            //清除截屏
            Log.d(Tag, "清除禁止截屏设置")
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    /**
     * 沉浸式状态栏
     */
    protected abstract fun setupTopBarLayout()

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