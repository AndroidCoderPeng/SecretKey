package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.bean.SecretTagBean
import com.pengxh.secretkey.utils.StringHelper

class SecretListAdapter constructor(
    mContext: Context?,
    list: ArrayList<SecretTagBean>?,
    allSecret: MutableList<SecretSQLiteBean>?
) :
    RecyclerView.Adapter<SecretListAdapter.ViewHolder>() {

    private var context = mContext
    private var dataBeans: ArrayList<SecretTagBean>? = list
    private var allSecretData: MutableList<SecretSQLiteBean>? = allSecret

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_secret_recyclerview, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = dataBeans!![position].title!!
        var type: String? = null
        allSecretData?.forEach {
            if (title == it.title) {
                type = it.category
                return@forEach
            }
        }
        holder.bindHolder(title, StringHelper.obtainResource(type))
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener {
                mOnItemClickListener!!.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataBeans == null) 0 else dataBeans!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var secretTypeView: ImageView = itemView.findViewById(R.id.secretTypeView)
        var secretTitle: TextView = itemView.findViewById(R.id.secretTitle)

        fun bindHolder(title: String, resource: Int) {
            secretTitle.text = title
            secretTypeView.setBackgroundResource(resource)
        }
    }

    private var mOnItemClickListener: OnCityItemClickListener? = null

    interface OnCityItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnCityItemClickListener(listener: OnCityItemClickListener?) {
        mOnItemClickListener = listener
    }
}