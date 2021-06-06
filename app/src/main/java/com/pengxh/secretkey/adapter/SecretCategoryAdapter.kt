package com.pengxh.secretkey.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pengxh.secretkey.BaseApplication
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.greendao.DaoSession
import com.pengxh.secretkey.greendao.SecretSQLiteBeanDao
import com.pengxh.secretkey.utils.Constant

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/7/1 15:10
 */
class SecretCategoryAdapter(ctx: Context) : BaseAdapter() {

    private var context: Context = ctx
    private var inflater: LayoutInflater
    private var daoSession: DaoSession = BaseApplication.instance().obtainDaoSession()

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int = Constant.IMAGES.size

    override fun getItem(position: Int): Any = Constant.IMAGES[position]

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
        itemViewHolder.secretCover.setImageResource(Constant.IMAGES[position])
        itemViewHolder.secretCategory.text = Constant.CATEGORY[position]
        val categoryData = daoSession.queryBuilder(SecretSQLiteBean::class.java)
            .where(SecretSQLiteBeanDao.Properties.Category.eq(Constant.CATEGORY[position]))
            .list()
        itemViewHolder.secretCount.text = "(${categoryData.size})"
        return view
    }

    class ItemViewHolder(v: View) {
        var secretCover: ImageView = v.findViewById(R.id.secretCover)
        var secretCategory: TextView = v.findViewById(R.id.secretCategory)
        var secretCount: TextView = v.findViewById(R.id.secretCount)
    }
}