package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.pengxh.app.multilib.widget.swipemenu.BaseSwipeListAdapter
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.OtherUtils

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/4 17:23
 */
class RecoverableAdapter(ctx: Context, list: MutableList<SecretBean>) : BaseSwipeListAdapter() {

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
        val itemViewHolder: ItemViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_swipe_listview, null)
            itemViewHolder = ItemViewHolder(view)
            view.tag = itemViewHolder
        } else {
            view = convertView
            itemViewHolder = view.tag as ItemViewHolder
        }
        val secretBean = beanList[position]
        itemViewHolder.recoverableView.setImageResource(OtherUtils.getImageResource(secretBean.secretCategory))
        itemViewHolder.secretTitle.text = secretBean.secretTitle
        itemViewHolder.secretAccount.text = secretBean.secretAccount
        itemViewHolder.deleteDateView.text = secretBean.deleteTime
        return view
    }

    class ItemViewHolder(v: View) {
        var recoverableView: ImageView = v.findViewById(R.id.recoverableView)
        var secretTitle: TextView = v.findViewById(R.id.secretTitle)
        var secretAccount: TextView = v.findViewById(R.id.secretAccount)
        var deleteDateView: TextView = v.findViewById(R.id.deleteDateView)
    }
}