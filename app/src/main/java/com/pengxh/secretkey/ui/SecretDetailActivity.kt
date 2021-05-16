package com.pengxh.secretkey.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import cn.bertsir.zbar.utils.QRUtils
import com.google.android.material.snackbar.Snackbar
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.ToastHelper
import com.pengxh.secretkey.widgets.InputDialogPlus
import com.pengxh.secretkey.widgets.ShareDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import kotlinx.android.synthetic.main.activity_secret_detail.*


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/3 10:48
 */
class SecretDetailActivity : BaseActivity() {

    private val context = this
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var sqLiteUtil: SQLiteUtil
    private lateinit var secretList: MutableList<SecretBean>
    private var category: String? = null

    override fun initLayoutView(): Int = R.layout.activity_secret_detail

    override fun setupTopBarLayout() {
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {
        category = intent.getStringExtra("mode")
        topLayout.setTitle(category).setTextColor(ContextCompat.getColor(this, R.color.white))

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        sqLiteUtil = SQLiteUtil()
    }

    override fun initEvent() {
        val snackBar = Snackbar.make(linearLayout, "长按账号和密码复制哦~", Snackbar.LENGTH_LONG)
        snackBar.setAction("知道了") { snackBar.dismiss() }
        snackBar.show()
    }

    override fun onResume() {
        super.onResume()
        secretList = sqLiteUtil.loadCategory(category!!)
        initUI(secretList)

        val secretDetailAdapter = SecretDetailAdapter(context, secretList)
        secretListView.adapter = secretDetailAdapter
        secretDetailAdapter.setOnItemClickListener(object :
            SecretDetailAdapter.OnChildViewClickListener {
            override fun onAccountLongPressed(index: Int) {
                val secretBean = secretList[index]
                val cipData = ClipData.newPlainText("secretAccount", secretBean.secretAccount)
                clipboardManager.setPrimaryClip(cipData)
                ToastHelper.showToast("账号复制成功", ToastHelper.SUCCESS)
            }

            override fun onPasswordLongPressed(index: Int) {
                val secretBean = secretList[index]
                val cipData = ClipData.newPlainText("secretPassword", secretBean.secretPassword)
                clipboardManager.setPrimaryClip(cipData)
                ToastHelper.showToast("密码复制成功", ToastHelper.SUCCESS)
            }

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

            override fun onModifyViewClicked(index: Int) {
                InputDialogPlus.Builder().setContext(context)
                    .setCategory(category)
                    .setOnDialogClickListener(object : InputDialogPlus.DialogClickListener {
                        override fun onConfirmClicked(
                            category: String,
                            password: String,
                            remarks: String
                        ) {
                            if (password == "") {
                                ToastHelper.showToast("不能将密码修改为空值", ToastHelper.WARING)
                                return
                            }
                            val secretBean = secretList[index]
                            sqLiteUtil.updateSecret(
                                secretBean.secretTitle!!,
                                secretBean.secretAccount!!,
                                category, password, remarks
                            )
                            finish()
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
        secretListView.setOnMenuItemClickListener { position, _, index ->
            val secretBean = secretList[position]
            when (index) {
                0 ->
                    QMUIDialog.MessageDialogBuilder(context)
                        .setTitle("温馨提示")
                        .setMessage("删除后将无法恢复，是否继续？")
                        .setCanceledOnTouchOutside(false)
                        .addAction("容我想想") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .addAction("已经想好") { dialog, _ ->
                            dialog.dismiss()
                            sqLiteUtil.deleteSecret(
                                secretBean.secretTitle!!,
                                secretBean.secretAccount!!
                            )
                            secretList.removeAt(position)
                            secretDetailAdapter.notifyDataSetChanged()
                            initUI(secretList)
                        }.create().show()
            }
            false
        }
        addSecretFab.attachToListView(secretListView)
        addSecretFab.setOnClickListener {
            val intent = Intent(this, AddSecretActivity::class.java)
            intent.putExtra("secretCategory", category)
            startActivity(intent)
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