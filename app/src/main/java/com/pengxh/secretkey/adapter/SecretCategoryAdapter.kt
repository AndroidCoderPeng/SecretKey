package com.pengxh.secretkey.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.Constant

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/1 15:10
 */
class SecretCategoryAdapter(ctx: Context, list: List<SecretBean>?) : BaseAdapter() {

    companion object {
        private const val Tag: String = "SecretCategoryAdapter"
    }

    private var context: Context = ctx
    private var inflater: LayoutInflater
    private var beanList: List<SecretBean>? = null

    init {
        inflater = LayoutInflater.from(context)
        beanList = list
    }

    override fun getCount(): Int = Constant.images.size

    override fun getItem(position: Int): Any = Constant.images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
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
        itemViewHolder.secretCover.setImageResource(Constant.images[position])
        itemViewHolder.secretCategory.text = Constant.title[position]
        if (beanList!!.isEmpty()) {
            //整张表都为空
            itemViewHolder.secretCount.text = "(0)"
        } else {
            /**
             * [{"recoverable":"1","secretAccount":"ABC","secretCategory":"网站","secretPassword":"123456789","secretTitle":"淘宝网"}]
             * */

        }
        return view
    }

    class ItemViewHolder(v: View) {
        var secretCover: ImageView = v.findViewById(R.id.secretCover)
        var secretCategory: TextView = v.findViewById(R.id.secretCategory)
        var secretCount: TextView = v.findViewById(R.id.secretCount)
    }
}