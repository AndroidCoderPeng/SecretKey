package com.pengxh.secretkey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.app.multilib.utils.FileUtil
import com.pengxh.app.multilib.utils.TimeUtil
import com.pengxh.secretkey.R
import java.io.File

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/24 20:43
 */
class FileManagerAdapter(ctx: Context, list: List<File>) :
    RecyclerView.Adapter<FileManagerAdapter.ItemViewHolder>() {

    private var context: Context = ctx
    private var fileList: List<File> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_file_recycleview, parent, false)
        )
    }

    override fun getItemCount(): Int = fileList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val file = fileList[position]
        holder.fileNameView.text = file.name
        holder.fileDateView.text = TimeUtil.timestampToTime(file.lastModified(), TimeUtil.DATE)
        holder.fileSizeView.text = FileUtil.formatFileSize(file.length()).replace("K", "KB")

        holder.childLayout.setOnClickListener {
            childClickListener!!.onClicked(position)
        }
    }

    class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var childLayout: RelativeLayout = itemView!!.findViewById(R.id.childLayout)
        var fileNameView: TextView = itemView!!.findViewById(R.id.fileNameView)
        var fileDateView: TextView = itemView!!.findViewById(R.id.fileDateView)
        var fileSizeView: TextView = itemView!!.findViewById(R.id.fileSizeView)
    }

    interface OnChildViewClickListener {
        fun onClicked(index: Int)
    }

    fun setOnChildViewClickListener(listener: OnChildViewClickListener) {
        this.childClickListener = listener
    }

    private var childClickListener: OnChildViewClickListener? = null
}