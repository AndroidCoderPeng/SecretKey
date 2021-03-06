package com.pengxh.secretkey.widgets

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.ToastHelper


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/12/23 10:44
 */
class SecretDetailDialog private constructor(builder: Builder) :
    AlertDialog(builder.mContext!!, R.style.DialogStyle) {

    private var ctx: Context? = null
    private var clipboard: ClipboardManager? = null
    private var title: String? = null
    private var account: String? = null
    private var password: String? = null
    private var marks: String? = null

    init {
        ctx = builder.mContext
        clipboard = ctx?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        title = builder.title
        account = builder.account
        password = builder.password
        marks = builder.marks
    }

    class Builder {
        var mContext: Context? = null
        var title: String? = null
        var account: String? = null
        var password: String? = null
        var marks: String? = null

        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setDialogTitle(s: String?): Builder {
            title = s
            return this
        }

        fun setMessage(s: String?): Builder {
            account = s
            return this
        }

        fun setSubMessage(s: String?): Builder {
            password = s
            return this
        }

        fun setSecretMarks(s: String?): Builder {
            marks = s
            return this
        }

        fun build(): SecretDetailDialog {
            return SecretDetailDialog(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_secret)
        initView()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    private fun initView() {
        findViewById<MarqueeTextView>(R.id.dialogTitle)?.text = title
        val dialogMessage = findViewById<MarqueeTextView>(R.id.dialogMessage)
        dialogMessage?.text = account
        dialogMessage?.setOnLongClickListener {
            val cipData = ClipData.newPlainText("account", account)
            clipboard?.setPrimaryClip(cipData)
            ToastHelper.showToast("账号复制成功", ToastHelper.SUCCESS)
            true
        }
        val dialogSubMessage = findViewById<TextView>(R.id.dialogSubMessage)
        dialogSubMessage?.text = password
        dialogSubMessage?.setOnLongClickListener {
            val cipData = ClipData.newPlainText("password", password)
            clipboard?.setPrimaryClip(cipData)
            ToastHelper.showToast("密码复制成功", ToastHelper.SUCCESS)
            true
        }
        findViewById<MarqueeTextView>(R.id.dialogMarks)?.text = marks
        findViewById<ImageView>(R.id.noticeImageView)?.setOnClickListener {
            ToastHelper.showToast("长按账号和密码可以复制", ToastHelper.DEFAULT)
        }
        //为ImageView添加属性动画
        val warningImageView = findViewById<ImageView>(R.id.warningImageView)
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(warningImageView, "alpha", 1f, 0f, 1f)
        animator.repeatCount = -1
        animator.duration = 3000
        animator.start()
    }
}