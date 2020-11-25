package com.pengxh.secretkey.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.FileManagerAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.ExcelHelper
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_file.*
import java.io.File


/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/24 20:26
 */
class FileManagerActivity : BaseNormalActivity() {

    companion object {
        private const val TAG: String = "FileManagerActivity"
    }

    private lateinit var sqLiteUtil: SQLiteUtil

    override fun initLayoutView(): Int = R.layout.activity_file

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        sqLiteUtil = SQLiteUtil(this)
    }

    override fun initEvent() {
        val dir = File(Environment.getExternalStorageDirectory(), "SecretKey")
        val fileList = dir.listFiles()!!.toList()

        val fileAdapter = FileManagerAdapter(this, fileList)
        fileRecyclerView.layoutManager = LinearLayoutManager(this)
        fileRecyclerView.addItemDecoration(DividerItemDecoration(this))
        fileRecyclerView.adapter = fileAdapter
        fileAdapter.setOnChildViewClickListener(object :
            FileManagerAdapter.OnChildViewClickListener {
            override fun onClicked(index: Int) {
                inputData(fileList[index].absolutePath)
            }
        })
    }

    /**
     * 导入数据
     * */
    private fun inputData(filePath: String) {
        val data = ExcelHelper.transformExcelToJson(filePath)
        val type = object : TypeToken<List<SecretBean>>() {}.type
        val beanList: List<SecretBean> = Gson().fromJson(data, type)
        AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle("温馨提示")
            .setMessage("可导入${beanList.size}条数据")
            .setCancelable(true)
            .setPositiveButton(
                "确认导入",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        beanList.forEach {
                            sqLiteUtil.saveSecret(
                                it.secretCategory!!,
                                it.secretTitle!!,
                                it.secretAccount!!,
                                it.secretPassword!!,
                                it.secretRemarks!!
                            )
                        }
                        EasyToast.showToast("导入成功", EasyToast.SUCCESS)
                    }
                })
            .create().show()
    }

    //RecyclerView分割线
    class DividerItemDecoration constructor(ctx: Context) : RecyclerView.ItemDecoration() {

        private val context: Context = ctx
        private var bottomLinePaint: Paint = Paint()

        init {
            bottomLinePaint.isAntiAlias = true
            bottomLinePaint.color = Color.LTGRAY
        }

        /**
         * 相当于itemView外还有一个矩形，在itemView上下左右设置空余部分，通过outRect 的left,top,right,bottom 来进行设置
         * 目标针对每一个item个体
         * */
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(0, 0, 0, 1)
        }

        /**
         * 在item间绘制，常用于绘制分割线
         * 针对整个Recyclerview 绘制需要循环遍历item子布局，然后方能针对具体的item进行增加绘制
         * */
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
            if (parent.layoutManager == null) {
                return
            }
            val left = parent.paddingLeft + DensityUtil.dp2px(context, 65.0f)
            val right = parent.width - parent.paddingRight - DensityUtil.dp2px(context, 20.0f)
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val top = child.bottom
                val bottom = top + 1
                c.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    bottomLinePaint
                )
            }
        }

        /**
         * 在item上绘制
         */
        override fun onDrawOver(
            c: Canvas,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.onDrawOver(c, parent, state)
        }
    }
}