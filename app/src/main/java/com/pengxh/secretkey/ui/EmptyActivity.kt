package com.pengxh.secretkey.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @description: 空白跳板页面，解决动态设置截屏开关
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/30 19:18
 */
class EmptyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}