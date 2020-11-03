package com.pengxh.secretkey.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import cn.bertsir.zbar.utils.QRUtils
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.utils.DensityUtil.dp2px
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem
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
        secretListView.adapter = secretDetailAdapter
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

            override fun onModifyViewClickListener(index: Int) {
                val secretBean = secretList[index]
                sqLiteUtil.updateSecret(secretBean.secretTitle!!,
                    secretBean.secretAccount!!,
                    secretBean.secretPassword!!)
            }
        })
        secretListView.setMenuCreator { menu ->
            val deleteItem = SwipeMenuItem(this)
            deleteItem.setIcon(R.drawable.ic_delete_white)
            deleteItem.background = ColorDrawable(Color.rgb(251, 81, 81))
            deleteItem.width = dp2px(this, 80.0f)
            deleteItem.title = "删除"
            deleteItem.titleSize = 18
            deleteItem.titleColor = Color.WHITE
            menu.addMenuItem(deleteItem)
        }
        secretListView.setOnMenuItemClickListener { position, menu, index ->
            val secretBean = secretList[position]
            when (index) {
                1 -> AlertView("温馨提示",
                    "删除后将无法恢复，是否继续？",
                    "容我想想",
                    arrayOf("已经想好"),
                    null,
                    this,
                    AlertView.Style.Alert,
                    OnItemClickListener { o: Any?, i: Int ->
                        if (i == 0) {
                            sqLiteUtil.deleteSecret(secretBean.secretTitle!!,
                                secretBean.secretAccount!!)
                            secretList.removeAt(position)
                            secretDetailAdapter.notifyDataSetChanged()
                            initUI(secretList)
                        }
                    }).setCancelable(false).show()
            }
            false
        }
    }

    private fun initUI(list: MutableList<SecretBean>) {
        if (list.size > 0) {
            secretListView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.VISIBLE
            secretListView.visibility = View.GONE
        }
    }
}