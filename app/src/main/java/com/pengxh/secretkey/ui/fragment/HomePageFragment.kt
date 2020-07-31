package com.pengxh.secretkey.ui.fragment

import android.graphics.Color
import cn.bertsir.zbar.QrConfig
import cn.bertsir.zbar.QrManager
import cn.bertsir.zbar.view.ScanLineView
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretCategoryAdapter
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020年7月24日12:41:36
 */
class HomePageFragment : BaseFragment() {

    private var length = 0 //进度条初始值

    override fun initLayoutView(): Int = R.layout.fragment_home

    override fun initData() {
        activity?.let {
            StatusBarColorUtil.setColor(it, ColorHelper.getXmlColor(it, R.color.colorAccent))
        }
        ImmersionBar.with(this).init()
    }

    override fun initEvent() {
        //进页面首先生成一次随机密码
        resetSecretNumber()

        //进度条
        initProgressBar()

        //扫一扫
        scanView.setOnClickListener {
            initScanner()
        }

        //密码分类九宫格
        secretGridView.adapter = context?.let { SecretCategoryAdapter(it) }
        secretGridView.setOnItemClickListener { parent, view, position, id ->

        }
    }

    private fun initProgressBar() {
        val timer = object : Timer() {}
        timer.schedule(object : TimerTask() {
            override fun run() {
                length++
                if (length == 60000) {
                    length = 0
                    //重置随机密码
                    resetSecretNumber()
                } else {
                    minuteProgressBar.progress = length
                }
            }
        }, 0, 1) //间隔调最小，这样进度条前进比较平滑
    }

    private fun resetSecretNumber() {
        firstView.text = OtherUtils.randomNumber().toString()
        secondView.text = OtherUtils.randomNumber().toString()
        thirdView.text = OtherUtils.randomNumber().toString()
        fourthView.text = OtherUtils.randomNumber().toString()
        fifthView.text = OtherUtils.randomNumber().toString()
        sixthView.text = OtherUtils.randomNumber().toString()
    }

    private fun initScanner() {
        val qrConfig = QrConfig.Builder().setTitleText("扫一扫") //设置Tilte文字
            .setShowLight(true) //显示手电筒按钮
            .setShowTitle(true) //显示Title
            .setShowAlbum(false) //显示从相册选择按钮
            .setCornerColor(ColorHelper.getXmlColor(context!!, R.color.colorPrimaryDark)) //设置扫描框颜色
            .setLineColor(ColorHelper.getXmlColor(context!!, R.color.colorPrimaryDark)) //设置扫描线颜色
            .setLineSpeed(QrConfig.LINE_MEDIUM) //设置扫描线速度
            .setDesText(null) //扫描框下文字
            .setShowDes(true) //是否显示扫描框下面文字
            .setPlaySound(true) //是否扫描成功后bi~的声音
            .setIsOnlyCenter(true) //是否只识别框中内容(默认为全屏识别)
            .setTitleBackgroudColor(ColorHelper.getXmlColor(context!!,
                R.color.colorAccent)) //设置状态栏颜色
            .setTitleTextColor(Color.WHITE) //设置Title文字颜色
            .setScreenOrientation(QrConfig.SCREEN_PORTRAIT) //设置屏幕方式
            .setScanLineStyle(ScanLineView.style_hybrid) //扫描线样式
            .setShowVibrator(true) //是否震动提醒
            .create()
        QrManager.getInstance().init(qrConfig).startScan(activity) { result ->
            EasyToast.showToast(result.content, EasyToast.SUCCESS)
        }
    }
}