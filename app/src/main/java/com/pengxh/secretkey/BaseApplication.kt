package com.pengxh.secretkey

import android.app.Application
import android.util.Log
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.utils.OtherUtils
import kotlin.properties.Delegates

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/6/11 14:24
 */
class BaseApplication : Application() {

    companion object {
        private const val TAG = "BaseApplication"

        //单例
        private var instance: BaseApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SaveKeyValues.initSharedPreferences(this)
        EasyToast.init(this)
        OtherUtils.init(this)
        //百度OCR初始化
        OCR.getInstance(this).initAccessToken(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken?) {
                Log.d(TAG, "onResult: ${result?.tokenJson}")
            }

            override fun onError(ocrError: OCRError?) {

            }
        }, this)
    }
}