package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretTagBean

class SecretListAdapter constructor(mContext: Context?, list: ArrayList<SecretTagBean>?) :
    RecyclerView.Adapter<SecretListAdapter.ViewHolder>() {

    private var context = mContext
    private var dataBeans: ArrayList<SecretTagBean>? = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_secret_recyclerview, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataBeans!![position].title?.let { holder.bindHolder(it) }
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(View.OnClickListener {
                mOnItemClickListener!!.onClick(position)
            })
        }
    }

    override fun getItemCount(): Int {
        return if (dataBeans == null) 0 else dataBeans!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var secretTitle: TextView = itemView.findViewById(R.id.secretTitle)

        fun bindHolder(title: String) {
            secretTitle.text = title
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