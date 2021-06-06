package com.pengxh.secretkey.adapter

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import com.pengxh.app.multilib.widget.swipemenu.BaseSwipeListAdapter
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.widgets.MarqueeTextView

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/3 12:35
 */
class SecretDetailAdapter(ctx: Context, list: MutableList<SecretSQLiteBean>) :
    BaseSwipeListAdapter() {

    private var context: Context = ctx
    private var beanList: MutableList<SecretSQLiteBean> = list
    private var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int = beanList.size

    override fun getItem(position: Int): Any = beanList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ItemViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_secret_listview, null)
            holder = ItemViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ItemViewHolder
        }
        val secretBean = beanList[position]
        holder.secretTitle.text = secretBean.title
        holder.secretAccount.text = secretBean.account
        holder.secretPassword.setText(secretBean.password)
        holder.secretRemarks.text = secretBean.remarks

        //账号密码长按事件
        holder.secretAccount.setOnLongClickListener {
            itemClickListener!!.onAccountLongPressed(position)
            true
        }
        holder.secretPassword.setOnLongClickListener {
            itemClickListener!!.onPasswordLongPressed(position)
            true
        }

        //点击事件
        holder.shareTextView.setOnClickListener {
            itemClickListener!!.onShareViewClicked(position)
        }
        holder.modifyTextView.setOnClickListener {
            itemClickListener!!.onModifyViewClicked(position)
        }
        holder.visibleView.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                holder.secretPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                holder.secretPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
        return view
    }

    class ItemViewHolder(itemView: View) {
        var secretTitle: MarqueeTextView = itemView.findViewById(R.id.secretTitle)
        var secretAccount: MarqueeTextView = itemView.findViewById(R.id.secretAccount)
        var secretPassword: EditText = itemView.findViewById(R.id.secretPassword)
        var secretRemarks: MarqueeTextView = itemView.findViewById(R.id.secretRemarks)

        //以下控件需要绑定点击事件
        var shareTextView: TextView = itemView.findViewById(R.id.shareTextView)
        var modifyTextView: TextView = itemView.findViewById(R.id.modifyTextView)
        var visibleView: ToggleButton = itemView.findViewById(R.id.visibleView)
    }

    interface OnChildViewClickListener {
        fun onAccountLongPressed(index: Int)

        fun onPasswordLongPressed(index: Int)

        fun onShareViewClicked(index: Int)

        fun onModifyViewClicked(index: Int)
    }

    fun setOnItemClickListener(listener: OnChildViewClickListener) {
        this.itemClickListener = listener
    }

    private var itemClickListener: OnChildViewClickListener? = null
}