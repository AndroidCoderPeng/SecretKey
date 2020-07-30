package com.pengxh.secretkey.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import com.pengxh.app.multilib.utils.SaveKeyValues
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.OtherUtils
import com.pengxh.secretkey.widgets.AgreementDialog
import com.pengxh.secretkey.widgets.FingerprintDialog
import pub.devrel.easypermissions.EasyPermissions
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/6/11 14:21
 */
class WelcomeActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val PERMISSIONS_CODE = 999
        private val USER_PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.USE_FINGERPRINT)
    }

    private lateinit var keyStore: KeyStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //*表示可变参数，比如Java中的 String... args
            if (EasyPermissions.hasPermissions(this, *USER_PERMISSIONS)) {
                startMainActivity()
            } else {
                AgreementDialog.Builder().setContext(this).setDialogTitle("使用须知")
                    .setDialogMessage("我们将严格按照上述协议为您提供服务，保护您的信息安全，点击“同意”即表示您已阅读并同意全部条款，可以继续使用本应用。")
                    .setOnDialogClickListener(object : AgreementDialog.OnDialogClickListener {
                        override fun onConfirmClick() {
                            EasyPermissions.requestPermissions(this@WelcomeActivity,
                                resources.getString(R.string.app_name) + "需要获取存储相关权限",
                                PERMISSIONS_CODE,
                                *USER_PERMISSIONS)
                        }

                        override fun onCancelClick() {
                            finish()
                        }
                    }).build().show()
            }
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        when (SaveKeyValues.getValue("mode", "numberSwitch") as String) {
            "numberSwitch" -> {
                val firstPassword = SaveKeyValues.getValue("firstPassword", "") as String
                if (firstPassword == "") {
                    startActivity(Intent(this, PasswordSetActivity::class.java))
                } else {
                    startActivity(Intent(this, PasswordCheckActivity::class.java))
                }
                finish()
            }
            "gestureSwitch" -> {
                val gesturePassword = SaveKeyValues.getValue("gesturePassword", "") as String
                if (gesturePassword == "") {
                    startActivity(Intent(this, GestureSetActivity::class.java))
                } else {
                    startActivity(Intent(this, GestureCheckActivity::class.java))
                }
                finish()
            }
            "fingerprintSwitch" -> {
                if (OtherUtils.isSupportFingerprint()) {
                    //创建参数
                    initKey()
                    initCipher()
                } else {
                    EasyToast.showToast("设备不支持指纹识别或者未录入指纹", EasyToast.ERROR)
                }
            }
        }
    }

    /**
     * 指纹识别成功
     * */
    fun onAuthenticated() {
        OtherUtils.intentActivity(MainActivity::class.java)
        finish()
    }

    /**
     * 取消指纹识别
     * */
    fun onFingerprintCancel() {
        finish()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startMainActivity()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * 初始化指纹Key
     * */
    private fun initKey() {
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
    private fun initCipher() {
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