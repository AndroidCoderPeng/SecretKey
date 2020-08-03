package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretBean

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/3 12:35
 */
class SecretDetailAdapter(ctx: Context, list: List<SecretBean>) :
    RecyclerView.Adapter<SecretDetailAdapter.ItemViewHolder>() {

    private var context: Context = ctx
    private var beanList: List<SecretBean> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_secret_recyclerview, parent, false))
    }

    override fun getItemCount(): Int = beanList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val secretBean = beanList[position]
        holder.secretCategory.text = secretBean.secretCategory
        holder.secretAccount.text = secretBean.secretAccount
        holder.secretPassword.text = secretBean.secretPassword
    }

    class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var secretCategory: TextView = itemView!!.findViewById(R.id.secretCategory)
        var secretAccount: TextView = itemView!!.findViewById(R.id.secretAccount)
        var secretPassword: TextView = itemView!!.findViewById(R.id.secretPassword)
    }
}