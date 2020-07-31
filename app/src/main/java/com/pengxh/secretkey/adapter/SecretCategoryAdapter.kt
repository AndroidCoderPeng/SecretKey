package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pengxh.secretkey.R

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/1 15:10
 */
class SecretCategoryAdapter(ctx: Context) : BaseAdapter() {

    companion object {
        val images = arrayOf(R.drawable.ic_web,
            R.drawable.ic_app,
            R.drawable.ic_game,
            R.drawable.ic_card,
            R.drawable.ic_work,
            R.drawable.ic_email,
            R.drawable.ic_chat,
            R.mipmap.other)
        val title = arrayOf("网站", "APP", "游戏", "银行卡", "工作", "邮件", "聊天", "其他")
    }

    private var context: Context = ctx
    private var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val itemViewHolder: ItemViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_secret_gridview, null)
            itemViewHolder = ItemViewHolder(view)
            view.tag = itemViewHolder
        } else {
            view = convertView
            itemViewHolder = view.tag as ItemViewHolder
        }
        itemViewHolder.secretCover.setImageResource(images[position])
        itemViewHolder.secretCategory.text = title[position]
        itemViewHolder.secretCount.text = "($position)"
        return view
    }

    class ItemViewHolder(v: View) {
        var secretCover: ImageView = v.findViewById(R.id.secretCover)
        var secretCategory: TextView = v.findViewById(R.id.secretCategory)
        var secretCount: TextView = v.findViewById(R.id.secretCount)
    }
}