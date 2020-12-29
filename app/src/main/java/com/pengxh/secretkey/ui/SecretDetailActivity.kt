package com.pengxh.secretkey.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import cn.bertsir.zbar.utils.QRUtils
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.widgets.InputDialog
import com.pengxh.secretkey.widgets.ShareDialog
import kotlinx.android.synthetic.main.activity_secret_detail.*
import kotlinx.android.synthetic.main.include_title_cyan.*


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
        val category = intent.getStringExtra("mode")
        mTitleView.text = category

        sqLiteUtil = SQLiteUtil()
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
            override fun onShareViewClicked(index: Int) {
                val secretBean = secretList[index]
                val data = secretBean.secretAccount + "\r\n" + secretBean.secretPassword
                val createCodeBitmap = QRUtils.getInstance().createQRCode(
                    data,
                    DensityUtil.dp2px(context, 300.0f),
                    DensityUtil.dp2px(context, 300.0f)
                )
                ShareDialog.Builder().setContext(context).setDialogTitle("请不要将此二维码随意泄露给他人")
                    .setDialogBitmap(createCodeBitmap).build().show()
            }

            override fun onCopyViewClicked(index: Int) {
                //复制的数据需要精练
                val secretBean = secretList[index]
                val data = secretBean.secretAccount + "\r\n" + secretBean.secretPassword
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val cipData = ClipData.newPlainText("Secret", data)
                // 将ClipData内容放到系统剪贴板里。
                clipboard.setPrimaryClip(cipData)
                EasyToast.showToast("账号密码复制成功", EasyToast.SUCCESS)
            }

            /**
             * 遗留问题：修改密码后无法立即生效，焦点（生命周期）问题
             * */
            override fun onModifyViewClicked(index: Int) {
                InputDialog.Builder().setContext(context).setDialogTitle("修改密码")
                    .setDialogMessage("请输入您的新密码")
                    .setOnDialogClickListener(object : InputDialog.DialogClickListener {
                        override fun onConfirmClicked(input: String) {
                            if (input == "") {
                                EasyToast.showToast("不能将密码修改为空值", EasyToast.WARING)
                                return
                            } else {
                                val secretBean = secretList[index]
                                sqLiteUtil.updateSecret(
                                    secretBean.secretTitle!!,
                                    secretBean.secretAccount!!,
                                    input
                                )
                                finish()
                            }
                        }
                    }).build().show(supportFragmentManager, "modifyPassword")
            }
        })
        secretListView.setMenuCreator { menu ->
            val deleteItem = SwipeMenuItem(this)
            deleteItem.setIcon(R.drawable.ic_delete_white)
            deleteItem.background = ColorDrawable(Color.rgb(251, 81, 81))
            deleteItem.width = DensityUtil.dp2px(this, 80.0f)
            deleteItem.title = "删除"
            deleteItem.titleSize = 18
            deleteItem.titleColor = Color.WHITE
            menu.addMenuItem(deleteItem)
        }
        secretListView.setOnMenuItemClickListener { position, menu, index ->
            val secretBean = secretList[position]
            when (index) {
                0 -> AlertDialog.Builder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("温馨提示")
                    .setMessage("删除后将无法恢复，是否继续？")
                    .setCancelable(false)
                    .setNegativeButton("容我想想", null)
                    .setPositiveButton("已经想好", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            sqLiteUtil.deleteSecret(
                                secretBean.secretTitle!!,
                                secretBean.secretAccount!!
                            )
                            secretList.removeAt(position)
                            secretDetailAdapter.notifyDataSetChanged()
                            initUI(secretList)
                        }
                    }).create().show()
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