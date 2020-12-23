package com.pengxh.secretkey.widgets

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pengxh.secretkey.R

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/12/23 10:44
 */
class SecretDetailDialog private constructor(builder: Builder) :
    AlertDialog(builder.mContext!!, R.style.DialogStyle) {

    private var ctx: Context? = null
    private var title: String? = null
    private var account: String? = null
    private var password: String? = null
    private var marks: String? = null

    init {
        ctx = builder.mContext
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
        findViewById<TextView>(R.id.dialogTitle)?.text = title
        findViewById<TextView>(R.id.dialogMessage)?.text = account
        findViewById<TextView>(R.id.dialogSubMessage)?.text = password
        findViewById<TextView>(R.id.dialogMarks)?.text = marks
    }
}