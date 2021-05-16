package com.pengxh.secretkey.widgets

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pengxh.secretkey.R
import com.pengxh.secretkey.ui.ExonerationActivity
import com.pengxh.secretkey.ui.PrivacyActivity
import com.pengxh.secretkey.utils.OtherUtils
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/24 10:44
 */
class AgreementDialog private constructor(builder: Builder) :
    AlertDialog(builder.mContext!!, R.style.DialogStyle), View.OnClickListener {

    private var ctx: Context? = null
    private var title: String? = null
    private var message: String? = null
    private var dialogListener: OnDialogClickListener? = null

    init {
        ctx = builder.mContext
        title = builder.title
        message = builder.message
        dialogListener = builder.listener
    }

    class Builder {
        var mContext: Context? = null
        var title: String? = null
        var message: String? = null
        var listener: OnDialogClickListener? = null
        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setDialogTitle(s: String?): Builder {
            title = s
            return this
        }

        fun setDialogMessage(s: String?): Builder {
            message = s
            return this
        }

        fun setOnDialogClickListener(listener: OnDialogClickListener?): Builder {
            this.listener = listener
            return this
        }

        fun build(): AgreementDialog {
            return AgreementDialog(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_agreement)
        initView()
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    private fun initView() {
        val dialogTitle = findViewById<TextView>(R.id.dialogTitle)
        dialogTitle?.text = title
        val dialogMessage = findViewById<TextView>(R.id.dialogMessage)
        dialogMessage?.text = message
        val subMessage = findViewById<TextView>(R.id.subMessage)
        //TODO 下划线点击效果
        if (subMessage != null) {
            val spanText = SpannableString(context.getString(R.string.agreement))
            //免责声明
            spanText.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    OtherUtils.intentActivity(ExonerationActivity::class.java)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.RED //设置文件颜色
                    ds.isUnderlineText = true //设置下划线
                }
            }, 17, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            subMessage.movementMethod = LinkMovementMethod.getInstance()
            subMessage.text = spanText

            //隐私政策
            spanText.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    OtherUtils.intentActivity(PrivacyActivity::class.java)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.RED //设置文件颜色
                    ds.isUnderlineText = true //设置下划线
                }
            }, spanText.length - 7, spanText.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            subMessage.movementMethod = LinkMovementMethod.getInstance()
            subMessage.text = spanText
        }
        val confirmButton = findViewById<QMUIRoundButton>(R.id.confirmButton)
        confirmButton?.setOnClickListener(this)
        confirmButton?.setChangeAlphaWhenPress(true)
        val cancelView = findViewById<TextView>(R.id.cancelView)
        cancelView?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.confirmButton) {
            dialogListener?.onConfirmClick()
        } else if (i == R.id.cancelView) {
            dialogListener?.onCancelClick()
        }
        dismiss()
    }

    interface OnDialogClickListener {
        fun onConfirmClick()
        fun onCancelClick()
    }
}