package com.pengxh.secretkey.utils

import android.content.Context

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/29 12:34
 */
class ColorHelper {
    companion object {
        //将APP内部colors.xml转为系统Color
        fun getXmlColor(context: Context, res: Int): Int = context.resources.getColor(res)
    }
}