package com.pengxh.secretkey.ui

import android.content.Intent
import android.graphics.Color
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import kotlinx.android.synthetic.main.activity_input.*
import kotlinx.android.synthetic.main.include_title_cyan.*
import java.io.File


/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/23 23:34
 */
class InputDataActivity : BaseActivity() {

    companion object {
        private const val TAG = "InputDataActivity"
    }

    override fun initLayoutView(): Int = R.layout.activity_input

    override fun initData() {
        mTitleView.text = "导入数据"
    }

    override fun initEvent() {
        val dir = File(Environment.getExternalStorageDirectory(), "SecretKey")
        val fileList = dir.listFiles()!!.toList()
        fileList.forEach { file ->
            val absolutePath = file.absolutePath
            Log.d(TAG, "initData: $absolutePath")
            if (absolutePath.contains("模板")) {
                val source = "模板路径：$absolutePath"
                val spanString = SpannableString(source)
                val span = ForegroundColorSpan(Color.BLUE)
                spanString.setSpan(span, 5, source.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                demoFilePathView.text = spanString
            }
        }
        //选择文件
        selectFile.setOnClickListener {
            //打开自定义文件管理器页面
            startActivity(
                Intent(this, FileManagerActivity::class.java)
            )
            finish()
        }
    }
}