package com.pengxh.secretkey.utils

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.fragment.app.FragmentManager
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.secretkey.widgets.FingerprintDialog
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/29 15:16
 */
class OtherUtils {
    companion object {
        private var mContext: Context? = null
        private lateinit var keyStore: KeyStore

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

        /**
         * 初始化指纹Key
         * */
        fun initKey() {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                val keyGenerator: KeyGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                val builder = KeyGenParameterSpec.Builder("fingerprint_key",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                keyGenerator.init(builder.build())
                keyGenerator.generateKey()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        /**
         * 初始化密码
         * */
        fun initCipher(supportFragmentManager: FragmentManager) {
            try {
                val key: SecretKey = keyStore.getKey("fingerprint_key", null) as SecretKey
                val cipher: Cipher =
                    Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
                cipher.init(Cipher.ENCRYPT_MODE, key)

                val fingerprintDialog = FingerprintDialog()
                fingerprintDialog.setCipher(cipher)
                fingerprintDialog.show(supportFragmentManager, "fingerprint")
            } catch (e: java.lang.Exception) {
                throw java.lang.RuntimeException(e)
            }
        }
    }
}