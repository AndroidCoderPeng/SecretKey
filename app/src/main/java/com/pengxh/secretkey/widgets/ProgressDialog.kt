package com.pengxh.secretkey.widgets

import android.app.AlertDialog
import android.content.Context
import com.pengxh.secretkey.R

class ProgressDialog private constructor(builder: Builder) :
    AlertDialog(builder.mContext!!, R.style.DialogStyle) {

    private var ctx: Context? = null
    private var title: String? = null

    init {
        ctx = builder.mContext
        title = builder.title
    }

    class Builder {

        var mContext: Context? = null
        var title: String? = null

        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setDialogTitle(s: String?): Builder {
            title = s
            return this
        }

        fun build(): ProgressDialog {
            return ProgressDialog(this)
        }
    }
}