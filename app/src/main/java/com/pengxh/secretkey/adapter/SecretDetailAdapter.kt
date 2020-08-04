package com.pengxh.secretkey.adapter

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretBean

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/3 12:35
 */
class SecretDetailAdapter(ctx: Context, list: MutableList<SecretBean>) :
    RecyclerView.Adapter<SecretDetailAdapter.ItemViewHolder>() {

    private var context: Context = ctx
    private var beanList: MutableList<SecretBean> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_secret_recyclerview, parent, false))
    }

    override fun getItemCount(): Int = beanList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val secretBean = beanList[position]
        holder.secretTitle.text = secretBean.secretTitle
        holder.secretAccount.text = secretBean.secretAccount
        holder.secretPassword.text = secretBean.secretPassword

        //点击事件
        holder.shareTextView.setOnClickListener {
            itemClickListener!!.onShareViewClickListener(position)
        }
        holder.copyTextView.setOnClickListener {
            itemClickListener!!.onCopyViewClickListener(position)
        }
        holder.deleteTextView.setOnClickListener {
            itemClickListener!!.onDeleteViewClickListener(position)
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
    }

    class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var secretTitle: TextView = itemView!!.findViewById(R.id.secretTitle)
        var secretAccount: TextView = itemView!!.findViewById(R.id.secretAccount)
        var secretPassword: TextView = itemView!!.findViewById(R.id.secretPassword)

        //以下控件需要绑定点击事件
        var shareTextView: TextView = itemView!!.findViewById(R.id.shareTextView)
        var copyTextView: TextView = itemView!!.findViewById(R.id.copyTextView)
        var deleteTextView: TextView = itemView!!.findViewById(R.id.deleteTextView)
        var visibleView: ToggleButton = itemView!!.findViewById(R.id.visibleView)
    }

    interface OnChildViewClickListener {
        fun onShareViewClickListener(index: Int)

        fun onCopyViewClickListener(index: Int)

        fun onDeleteViewClickListener(index: Int)
    }

    fun setOnItemClickListener(listener: OnChildViewClickListener) {
        this.itemClickListener = listener
    }

    private var itemClickListener: OnChildViewClickListener? = null
}