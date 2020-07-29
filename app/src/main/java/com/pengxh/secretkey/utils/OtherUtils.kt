package com.pengxh.secretkey.utils

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import com.pengxh.app.multilib.base.BaseNormalActivity


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/29 15:16
 */
class OtherUtils {
    companion object {
        private var mContext: Context? = null

        fun init(context: Context) {
            mContext = context.applicationContext //获取全局上下文，最长生命周期
        }

        fun <T : BaseNormalActivity?> intentActivity(clazz: Class<T>?, mode: String?) {
            val intent = Intent(mContext, clazz)
            intent.putExtra("mode", mode)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(intent)
        }

        fun <T : BaseNormalActivity?> intentActivity(clazz: Class<T>?) {
            val intent = Intent(mContext, clazz)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext!!.startActivity(intent)
        }

        /**
         * 是否支持指纹
         */
        fun isSupportFingerprint(): Boolean {
            val result: Boolean
            result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val fingerprintManager =
                    mContext!!.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints();
            } else {
                false
            }
            return result
        }
    }
}