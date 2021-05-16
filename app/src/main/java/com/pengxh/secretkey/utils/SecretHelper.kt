package com.pengxh.secretkey.utils

import android.util.Base64

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 采用Base64编码方式加码，但是加密前已将原始数据进行转换，提高安全性
 * @date: 2020/8/5 14:45
 */
class SecretHelper {
    companion object {
        /**
         * 加密
         * */
        fun encode(originalData: String): String {
            return String(
                Base64.encode(originalData.toByteArray(Charsets.UTF_8), Base64.NO_WRAP),
                Charsets.UTF_8
            )
        }

        /**
         * 解密
         * */
        fun decode(encodeData: String): String {
            return String(Base64.decode(encodeData, Base64.NO_WRAP))
        }
    }
}