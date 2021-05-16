package com.pengxh.secretkey

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.os.Bundle
import android.util.Log
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.pengxh.app.multilib.utils.BroadcastManager
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.utils.ToastHelper
import kotlin.properties.Delegates

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/6/11 14:24
 */
class BaseApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private const val Tag = "BaseApplication"

        //单例
        private var instance: BaseApplication by Delegates.notNull()
        fun instance() = instance
    }

    private var isBackground = false

    override fun onCreate() {
        super.onCreate()
        instance = this
        SaveKeyValues.initSharedPreferences(this)
        ToastHelper.initToastHelper(this)
        OtherUtils.init(this)
        registerActivityLifecycleCallbacks(this)
        //百度OCR初始化
        OCR.getInstance(this).initAccessToken(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken?) {
                Log.d(Tag, "onResult: ${result?.tokenJson}")
            }

            override fun onError(ocrError: OCRError?) {

            }
        }, this)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityResumed(activity: Activity) {
        if (isBackground) {
            isBackground = false
            Log.d(Tag, "APP回到了前台")
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true
            Log.d(Tag, "APP回到桌面")
            BroadcastManager.getInstance(this).sendBroadcast("finishActivity", "")
        }
    }
}