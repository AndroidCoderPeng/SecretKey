package com.pengxh.secretkey.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bertsir.zbar.utils.QRUtils
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.utils.DensityUtil.dp2px
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.ShareDialog
import kotlinx.android.synthetic.main.activity_secret_detail.*
import kotlinx.android.synthetic.main.include_title_white.*


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/3 10:48
 */
class SecretDetailActivity : BaseActivity() {

    companion object {
        private const val Tag = "SecretDetailActivity"
    }

    private val context = this
    private lateinit var sqLiteUtil: SQLiteUtil
    private lateinit var secretList: MutableList<SecretBean>

    override fun initLayoutView(): Int = R.layout.activity_secret_detail

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        val category = intent.getStringExtra("mode")
        mTitleView.text = category

        sqLiteUtil = SQLiteUtil(context)
        secretList = sqLiteUtil.loadCategory(category!!)
    }

    override fun initEvent() {
        initUI(secretList)
    }

    override fun onResume() {
        super.onResume()
        val secretDetailAdapter = SecretDetailAdapter(context, secretList)
        secretRecyclerView.layoutManager = LinearLayoutManager(context)
        secretRecyclerView.adapter = secretDetailAdapter
        secretDetailAdapter.setOnItemClickListener(object :
            SecretDetailAdapter.OnChildViewClickListener {
            override fun onShareViewClickListener(index: Int) {
                val secretBean = secretList[index]
                val data = secretBean.secretAccount + "\r\n" + secretBean.secretPassword
                val createCodeBitmap = QRUtils.getInstance()
                    .createQRCode(data, dp2px(context, 300.0f), dp2px(context, 300.0f))
                ShareDialog.Builder().setContext(context).setDialogTitle("请不要将此二维码随意泄露给他人")
                    .setDialogBitmap(createCodeBitmap).build().show()
            }

            override fun onCopyViewClickListener(index: Int) {
                //复制的数据需要精练
                val secretBean = secretList[index]
                val data = secretBean.secretAccount + "\r\n" + secretBean.secretPassword
                Log.d(Tag, "密码数据Json: $data")
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
                            val secretBean = secretList[index]
                            sqLiteUtil.deleteSecret(secretBean.secretTitle!!,
                                secretBean.secretAccount!!)
                            secretList.removeAt(index)
                            secretDetailAdapter.notifyDataSetChanged()
                            initUI(secretList)
                        }
                    }).setCancelable(false).show()
            }
        })
    }

    private fun initUI(list: MutableList<SecretBean>) {
        if (list.size > 0) {
            secretRecyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.VISIBLE
            secretRecyclerView.visibility = View.GONE
        }
    }
}