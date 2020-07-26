package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pengxh.secretkey.R

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/1 15:10
 */
class KeyBoardAdapter(ctx: Context, array: Array<String>) : BaseAdapter() {

    private var context: Context = ctx
    private var keyArray: Array<String> = array
    private var inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int = keyArray.size

    override fun getItem(position: Int): Any = keyArray[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val itemViewHolder: ItemViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_keyboard, null)
            itemViewHolder = ItemViewHolder(view)
            view.tag = itemViewHolder
        } else {
            view = convertView
            itemViewHolder = view.tag as ItemViewHolder
        }
        itemViewHolder.keyboard.text = keyArray[position]
        return view
    }

    class ItemViewHolder(v: View) {
        var keyboard: TextView = v.findViewById(R.id.keyboard)
    }
}