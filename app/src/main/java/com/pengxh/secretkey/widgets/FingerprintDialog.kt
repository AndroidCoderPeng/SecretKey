package com.pengxh.secretkey.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.WelcomeActivity
import javax.crypto.Cipher


/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/29 21:19
 */
class FingerprintDialog : DialogFragment() {

    private var fingerprintManager: FingerprintManager? = null
    private var mCancellationSignal: CancellationSignal? = null
    private var mCipher: Cipher? = null
    private var isSelfCancelled = false //标识是否是用户主动取消的认证。
    private var fingerprintHint: TextView? = null
    private var fingerprintView: ImageView? = null
    private var mActivity: WelcomeActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity as WelcomeActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fingerprintManager = mActivity!!.getSystemService(FingerprintManager::class.java)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fingerprint, container, false)
        fingerprintView = view.findViewById(R.id.fingerprintView)
        fingerprintHint = view.findViewById(R.id.fingerprintHint)
        val fingerprintCancel = view.findViewById<TextView>(R.id.fingerprintCancel)
        fingerprintCancel.setOnClickListener { v: View? ->
            dismiss()
            stopListening()
            mActivity?.onFingerprintCancel()
        }
        return view
    }

    /**
     * 初始化窗口，比如圆角，宽高等
     * */
    override fun onStart() {
        super.onStart()
        val win: Window = dialog!!.window ?: return
        win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win.attributes
        params.width = DensityUtil.dp2px(context, 250.0f)
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params;
    }

    fun setCipher(cipher: Cipher?) {
        mCipher = cipher
    }

    override fun onResume() {
        super.onResume()
        // 开始指纹认证监听
        startListening(mCipher)
    }

    private fun startListening(cipher: Cipher?) {
        isSelfCancelled = false
        mCancellationSignal = CancellationSignal()
        fingerprintManager!!.authenticate(FingerprintManager.CryptoObject(cipher!!),
            mCancellationSignal,
            0,
            object : FingerprintManager.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (!isSelfCancelled) {
                        fingerprintHint!!.text = errString
                        if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                            Toast.makeText(mActivity, errString, Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }

                override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                    super.onAuthenticationHelp(helpCode, helpString)
                    fingerprintHint!!.text = helpString
                }

                override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fingerprintView!!.setBackgroundResource(R.mipmap.fingerprint_ok)
                    fingerprintHint!!.text = "指纹认证成功"
                    handler.sendEmptyMessageDelayed(1, 500)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    fingerprintView!!.setBackgroundResource(R.mipmap.fingerprint_error)
                    fingerprintHint!!.text = "指纹认证失败，请再试一次"
                }
            },
            null)
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                dismiss()
                mActivity?.onAuthenticated()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // 停止指纹认证监听
        stopListening()
    }

    private fun stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal!!.cancel()
            mCancellationSignal = null
            isSelfCancelled = true
        }
    }
}