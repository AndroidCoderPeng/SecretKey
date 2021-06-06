package com.pengxh.secretkey.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Environment
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.app.multilib.utils.BroadcastManager
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.BaseApplication
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.FileManagerAdapter
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.greendao.SecretSQLiteBeanDao
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.ExcelHelper
import com.pengxh.secretkey.utils.ToastHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import kotlinx.android.synthetic.main.activity_file.*
import java.io.File


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/24 20:26
 */
class FileManagerActivity : BaseActivity() {

    private var secretBeanDao: SecretSQLiteBeanDao =
        BaseApplication.instance().obtainDaoSession().secretSQLiteBeanDao
    private var fileList: MutableList<File> = ArrayList()
    private lateinit var fileAdapter: FileManagerAdapter

    override fun initLayoutView(): Int = R.layout.activity_file

    override fun setupTopBarLayout() {
        topLayout.setTitle("选择导入文件").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
        topLayout.setTitleGravity(Gravity.START)
        topLayout.addLeftImageButton(R.drawable.ic_left, 0).setOnClickListener { finish() }
    }

    override fun initData() {
        val dir = File(Environment.getExternalStorageDirectory(), "SecretKey")
        fileList = dir.listFiles()!!.toMutableList()
        fileAdapter = FileManagerAdapter(this, fileList)
        fileRecyclerView.layoutManager = LinearLayoutManager(this)
        fileRecyclerView.addItemDecoration(DividerItemDecoration(this))
        fileRecyclerView.adapter = fileAdapter

    }

    override fun initEvent() {
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
        val type = object : TypeToken<List<SecretSQLiteBean>>() {}.type
        val beanList: List<SecretSQLiteBean> = Gson().fromJson(data, type)
        QMUIDialog.MessageDialogBuilder(this)
            .setTitle("温馨提示")
            .setMessage("可导入${beanList.size}条数据。导入的账号数据如与密码管家已有账号数据重复，将会自动修改原有账号密码，请谨慎操作！")
            .setCanceledOnTouchOutside(true)
            .addAction("知道了") { dialog, _ ->
                dialog.dismiss()
                beanList.forEach {
                    secretBeanDao.insert(it)
                }
                ToastHelper.showToast("导入成功", ToastHelper.SUCCESS)
                finish()
                //通知列表页刷新数据
                BroadcastManager.getInstance(this@FileManagerActivity)
                    .sendBroadcast(Constant.ACTION_UPDATE, "updateData")
            }.create().show()
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
    }
}