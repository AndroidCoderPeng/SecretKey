package com.pengxh.secretkey.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.Constant
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/6 22:12
 */
class InputDialogPlus private constructor(builder: Builder) : DialogFragment() {

    private var mRootView: View? = null
    private var ctx: Context? = null
    private var dialogListener: DialogClickListener? = null
    private var category: String? = null

    init {
        ctx = builder.mContext
        category = builder.mCategory
        dialogListener = builder.listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.InputDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //对话框的布局
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_input_plus, container, false);
        }
        val inputCategory: Spinner = mRootView!!.findViewById(R.id.inputCategory)
        //选中当前分类
        for (index in 0..Constant.CATEGORY.size) {
            if (category == Constant.CATEGORY[index]) {
                inputCategory.setSelection(index, true)
                break
            }
        }
        inputCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = Constant.CATEGORY[position]
            }
        }

        val inputPassword: EditText = mRootView!!.findViewById(R.id.inputPassword)
        val inputMarks: EditText = mRootView!!.findViewById(R.id.inputMarks)

        val confirmButton: QMUIRoundButton = mRootView!!.findViewById(R.id.confirmButton)
        confirmButton.setChangeAlphaWhenPress(true)
        confirmButton.setOnClickListener {
            dialogListener!!.onConfirmClicked(
                category!!,
                inputPassword.text.toString(),
                inputMarks.text.toString()
            )
            dismiss()
        }
        isCancelable = true
        return mRootView
    }

    override fun onResume() {
        super.onResume()
        val width = DensityUtil.getDisplaySize(ctx)["HorizontalPixels"]
        val window = dialog?.window!!
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(
            (width!! * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    class Builder {
        var mContext: Context? = null
        var mCategory: String? = null
        var listener: DialogClickListener? = null

        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setCategory(s: String?): Builder {
            mCategory = s
            return this
        }

        fun setOnDialogClickListener(l: DialogClickListener?): Builder {
            listener = l
            return this
        }

        fun build(): InputDialogPlus {
            return InputDialogPlus(this)
        }
    }

    interface DialogClickListener {
        fun onConfirmClicked(category: String, password: String, remarks: String)
    }
}