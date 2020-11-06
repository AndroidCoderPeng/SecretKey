package com.pengxh.secretkey.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.R


/**
 * @description: TODO 带输入框的对话框
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/6 22:12
 */
class InputDialog private constructor(builder: Builder) : DialogFragment() {

    private var mRootView: View? = null

    private var ctx: Context? = null
    private var title: String? = null
    private var hintMessage: String? = null
    private var dialogListener: DialogClickListener? = null

    init {
        ctx = builder.mContext
        title = builder.mTitle
        hintMessage = builder.mHintMessage
        dialogListener = builder.listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.InputDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val window = dialog?.window!!
        window.decorView.setPadding(30, 0, 30, 0)
        val lp = window.attributes
        lp.height = DensityUtil.dp2px(ctx, 150.0f)
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //对话框的布局
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_input, container, false);
        }
        val mDialogTitle: TextView = mRootView!!.findViewById(R.id.mDialogTitle)
        val mInputHint: EditText = mRootView!!.findViewById(R.id.mInputHint)

        mDialogTitle.text = title
        mInputHint.hint = hintMessage

        val mDialogConfirm: TextView = mRootView!!.findViewById(R.id.mDialogConfirm)
        mDialogConfirm.setOnClickListener {
            dialogListener!!.onConfirmClicked(mInputHint.text.toString())
            dismiss()
        }
        isCancelable = true
        return mRootView
    }

    class Builder {
        var mContext: Context? = null
        var mTitle: String? = null
        var mHintMessage: String? = null
        var listener: DialogClickListener? = null

        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setDialogTitle(s: String?): Builder {
            mTitle = s
            return this
        }

        fun setDialogMessage(s: String?): Builder {
            mHintMessage = s
            return this
        }

        fun setOnDialogClickListener(l: DialogClickListener?): Builder {
            listener = l
            return this
        }

        fun build(): InputDialog {
            return InputDialog(this)
        }
    }

    interface DialogClickListener {
        fun onConfirmClicked(input: String)
    }
}