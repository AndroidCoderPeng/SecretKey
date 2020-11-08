package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.UpdateLogBean

class UpdateViewAdapter(ctx: Context, list: ArrayList<UpdateLogBean>) : BaseAdapter() {

    private var context: Context = ctx
    private var beanList: ArrayList<UpdateLogBean> = list

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
            view = inflater.inflate(R.layout.item_update_listview, null)
            holder = ItemViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ItemViewHolder
        }
        val updateLogBean = beanList[position]

        holder.updateMessage.text = updateLogBean.message
        holder.updateTime.text = updateLogBean.updateTime
        return view
    }

    class ItemViewHolder(itemView: View) {
        var updateMessage: TextView = itemView.findViewById(R.id.updateMessage)
        var updateTime: TextView = itemView.findViewById(R.id.updateTime)
    }
}