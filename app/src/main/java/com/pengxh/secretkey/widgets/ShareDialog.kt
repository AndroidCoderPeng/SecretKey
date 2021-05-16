package com.pengxh.secretkey.widgets

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pengxh.secretkey.R

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/24 10:44
 */
class ShareDialog private constructor(builder: Builder) :
    AlertDialog(builder.mContext!!, R.style.DialogStyle) {

    private var ctx: Context? = null
    private var title: String? = null
    private var bitmap: Bitmap? = null

    init {
        ctx = builder.mContext
        title = builder.title
        bitmap = builder.bitmap
    }

    class Builder {
        var mContext: Context? = null
        var title: String? = null
        var bitmap: Bitmap? = null

        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setDialogTitle(s: String?): Builder {
            title = s
            return this
        }

        fun setDialogBitmap(map: Bitmap?): Builder {
            bitmap = map
            return this
        }

        fun build(): ShareDialog {
            return ShareDialog(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_share)
        initView()
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    private fun initView() {
        val shareTitle = findViewById<TextView>(R.id.shareTitle)
        shareTitle?.text = title
        val codeView = findViewById<ImageView>(R.id.codeView)
        codeView?.setImageBitmap(bitmap)
    }
}