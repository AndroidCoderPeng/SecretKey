package com.pengxh.secretkey.adapter

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import com.pengxh.app.multilib.widget.swipemenu.BaseSwipeListAdapter
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretBean

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/3 12:35
 */
class SecretDetailAdapter(ctx: Context, list: MutableList<SecretBean>) : BaseSwipeListAdapter() {

    private var context: Context = ctx
    private var beanList: MutableList<SecretBean> = list
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
        holder.secretTitle.text = secretBean.secretTitle
        holder.secretAccount.text = secretBean.secretAccount
        holder.secretPassword.text = secretBean.secretPassword
        holder.secretRemarks.text = secretBean.secretRemarks

        //点击事件
        holder.shareTextView.setOnClickListener {
            itemClickListener!!.onShareViewClicked(position)
        }
        holder.copyTextView.setOnClickListener {
            itemClickListener!!.onCopyViewClicked(position)
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
        var secretTitle: TextView = itemView.findViewById(R.id.secretTitle)
        var secretAccount: TextView = itemView.findViewById(R.id.secretAccount)
        var secretPassword: TextView = itemView.findViewById(R.id.secretPassword)
        var secretRemarks: TextView = itemView.findViewById(R.id.secretRemarks)

        //以下控件需要绑定点击事件
        var shareTextView: TextView = itemView.findViewById(R.id.shareTextView)
        var copyTextView: TextView = itemView.findViewById(R.id.copyTextView)
        var modifyTextView: TextView = itemView.findViewById(R.id.modifyTextView)
        var visibleView: ToggleButton = itemView.findViewById(R.id.visibleView)
    }

    interface OnChildViewClickListener {
        fun onShareViewClicked(index: Int)

        fun onCopyViewClicked(index: Int)

        fun onModifyViewClicked(index: Int)
    }

    fun setOnItemClickListener(listener: OnChildViewClickListener) {
        this.itemClickListener = listener
    }

    private var itemClickListener: OnChildViewClickListener? = null
}