package com.pengxh.secretkey.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bertsir.zbar.utils.QRUtils
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.app.multilib.utils.TimeUtil
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.ShareDialog
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_secret_detail.emptyLayout
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/5 10:15
 */
class SearchEventActivity : BaseActivity() {

    private val context = this
    private lateinit var sqLiteUtil: SQLiteUtil

    override fun initLayoutView(): Int = R.layout.activity_search

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "搜索结果"
        sqLiteUtil = SQLiteUtil(context)
    }

    override fun initEvent() {
        val keyWords = intent.getStringExtra("mode")
        val beanList = sqLiteUtil.loadAccountSecret(keyWords!!)
        initUI(beanList)
        val secretDetailAdapter = SecretDetailAdapter(this, beanList)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.adapter = secretDetailAdapter
        secretDetailAdapter.setOnItemClickListener(object :
            SecretDetailAdapter.OnChildViewClickListener {
            override fun onShareViewClickListener(index: Int) {
                val secretBean = beanList[index]
                val data = secretBean.secretAccount + "\r\n" + secretBean.secretPassword
                val createCodeBitmap = QRUtils.getInstance().createQRCode(data,
                    DensityUtil.dp2px(context, 300.0f),
                    DensityUtil.dp2px(context, 300.0f))
                ShareDialog.Builder().setContext(context).setDialogTitle("请不要将此二维码随意泄露给他人")
                    .setDialogBitmap(createCodeBitmap).build().show()
            }

            override fun onCopyViewClickListener(index: Int) {
                //复制的数据需要精练
                val secretBean = beanList[index]
                val data = secretBean.secretAccount + "\r\n" + secretBean.secretPassword
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val cipData = ClipData.newPlainText("Secret", data)
                // 将ClipData内容放到系统剪贴板里。
                clipboard.setPrimaryClip(cipData);
                EasyToast.showToast("密码复制成功", EasyToast.SUCCESS)
            }

            override fun onDeleteViewClickListener(index: Int) {
                AlertView("温馨提示",
                    "确定删除此账号密码？",
                    "容我想想",
                    arrayOf("已经想好"),
                    null,
                    context,
                    AlertView.Style.Alert,
                    OnItemClickListener { o, position ->
                        if (position == 0) {
                            //先删除数据库数据，再删除List，不然会出现角标越界
                            val secretBean = beanList[index]
                            sqLiteUtil.deleteSecret(secretBean.secretTitle!!,
                                secretBean.secretAccount!!,
                                TimeUtil.timestampToTime(System.currentTimeMillis(), TimeUtil.TIME))
                            beanList.removeAt(index)
                            secretDetailAdapter.notifyDataSetChanged()
                            initUI(beanList)
                        }
                    }).setCancelable(false).show()
            }
        })
    }

    private fun initUI(list: MutableList<SecretBean>) {
        if (list.size > 0) {
            searchRecyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.VISIBLE
            searchRecyclerView.visibility = View.GONE
        }
    }
}