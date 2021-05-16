package com.pengxh.secretkey.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import cn.bertsir.zbar.QrConfig
import cn.bertsir.zbar.QrManager
import cn.bertsir.zbar.view.ScanLineView
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretCategoryAdapter
import com.pengxh.secretkey.ui.AddSecretActivity
import com.pengxh.secretkey.ui.SearchEventActivity
import com.pengxh.secretkey.ui.SecretDetailActivity
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.SQLiteUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_secret_number.*
import kotlinx.android.synthetic.main.include_title_main.*
import java.util.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020年7月24日12:41:36
 */
class HomePageFragment : BaseFragment() {

    companion object {
        private const val Tag: String = "HomePageFragment"
    }

    private var length = 0 //进度条初始值
    private lateinit var sqLiteUtil: SQLiteUtil

    override fun initLayoutView(): Int = R.layout.fragment_home

    override fun initData() {
        sqLiteUtil = SQLiteUtil()

        attentionMessage.isSelected = true
    }

    override fun initEvent() {
        //TODO 搜索，还没找到合适的UI效果，暂时隐藏
        mTitleRightView.setOnClickListener {
            val key = searchView.text.toString().trim()
            if (key == "") {
                EasyToast.showToast("您什么都还没输入呢~", EasyToast.WARING)
                return@setOnClickListener
            }
            OtherUtils.intentActivity(SearchEventActivity::class.java, key)
        }
        searchView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val s = searchView.text.toString().trim()
                    Log.d(Tag, "onEditorAction: $s")
                    //点击搜索的时候隐藏软键盘
                    hideKeyboard(searchView)
                    OtherUtils.intentActivity(SearchEventActivity::class.java, s)
                    return true
                }
                return false
            }
        })

        //进页面首先生成一次随机密码
        resetSecretNumber()

        //进度条
        initProgressBar()

        //扫一扫
        scanView.setOnClickListener {
            initScanner()
        }

        //添加密码
        outlineFab.setOnClickListener {
            OtherUtils.intentActivity(AddSecretActivity::class.java)
        }
    }

    private fun hideKeyboard(view: View) {
        val manager: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        //密码分类九宫格
        secretGridView.adapter = context?.let { SecretCategoryAdapter(it) }
        secretGridView.setOnItemClickListener { _, _, position, _ ->
            OtherUtils.intentActivity(SecretDetailActivity::class.java, Constant.CATEGORY[position])
        }
    }

    private fun initProgressBar() {
        object : Timer() {}.schedule(object : TimerTask() {
            override fun run() {
                length++
                if (length == 50000) {
                    length = 0
                    //重置随机密码
                    handler.sendEmptyMessage(20210302)
                } else {
                    minuteProgressBar?.progress = length
                }
            }
        }, 0, 1) //间隔调最小，这样进度条前进比较平滑
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 20210302) {
                resetSecretNumber()
            }
        }
    }

    private fun resetSecretNumber() {
        if (firstView != null) {
            firstView.text = OtherUtils.randomNumber()
        }
        if (secondView != null) {
            secondView.text = OtherUtils.randomNumber()
        }
        if (thirdView != null) {
            thirdView.text = OtherUtils.randomNumber()
        }
        if (fourthView != null) {
            fourthView.text = OtherUtils.randomNumber()
        }
        if (fifthView != null) {
            fifthView.text = OtherUtils.randomNumber()
        }
        if (sixthView != null) {
            sixthView.text = OtherUtils.randomNumber()
        }
    }

    private fun initScanner() {
        val qrConfig = QrConfig.Builder().setTitleText("扫一扫") //设置Title文字
            .setShowLight(true) //显示手电筒按钮
            .setShowTitle(true) //显示Title
            .setScanType(QrConfig.TYPE_ALL)//识别二维码和条形码
            .setShowAlbum(false) //显示从相册选择按钮
            .setCornerColor(ColorHelper.getXmlColor(context!!, R.color.colorPrimaryDark)) //设置扫描框颜色
            .setLineColor(ColorHelper.getXmlColor(context!!, R.color.colorPrimaryDark)) //设置扫描线颜色
            .setLineSpeed(QrConfig.LINE_MEDIUM) //设置扫描线速度
            .setDesText(null) //扫描框下文字
            .setShowDes(true) //是否显示扫描框下面文字
            .setPlaySound(true) //是否扫描成功后bi~的声音
            .setIsOnlyCenter(true) //是否只识别框中内容(默认为全屏识别)
            .setTitleBackgroudColor(
                ColorHelper.getXmlColor(
                    context!!,
                    R.color.mainThemeColor
                )
            ) //设置状态栏颜色
            .setTitleTextColor(Color.WHITE) //设置Title文字颜色
            .setScreenOrientation(QrConfig.SCREEN_PORTRAIT) //设置屏幕方式
            .setScanLineStyle(ScanLineView.style_hybrid) //扫描线样式
            .setShowVibrator(true) //是否震动提醒
            .create()
        QrManager.getInstance().init(qrConfig).startScan(activity) { result ->
            OtherUtils.showAlertDialog(context!!, "扫描结果", result.content)
        }
    }
}