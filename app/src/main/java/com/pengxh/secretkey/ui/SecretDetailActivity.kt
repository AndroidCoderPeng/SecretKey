package com.pengxh.secretkey.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.core.content.ContextCompat
import cn.bertsir.zbar.utils.QRUtils
import com.google.android.material.snackbar.Snackbar
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.BaseApplication
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretDetailAdapter
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.greendao.DaoSession
import com.pengxh.secretkey.greendao.SecretSQLiteBeanDao
import com.pengxh.secretkey.utils.ToastHelper
import com.pengxh.secretkey.widgets.InputDialogPlus
import com.pengxh.secretkey.widgets.ShareDialog
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import kotlinx.android.synthetic.main.activity_secret_detail.*
import java.lang.ref.WeakReference


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/3 10:48
 */
class SecretDetailActivity : BaseActivity() {

    private var daoSession: DaoSession = BaseApplication.instance().obtainDaoSession()
    private var secretList: MutableList<SecretSQLiteBean> = ArrayList()
    private var category: String? = null
    private var isUpdate: Boolean = false
    private lateinit var secretListAdapter: SecretDetailAdapter
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private lateinit var clipboardManager: ClipboardManager

    override fun initLayoutView(): Int = R.layout.activity_secret_detail

    override fun setupTopBarLayout() {
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {
        weakReferenceHandler = WeakReferenceHandler(this)
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        category = intent.getStringExtra("mode")
        topLayout.setTitle(category).setTextColor(ContextCompat.getColor(this, R.color.white))

        secretList = daoSession.queryBuilder(SecretSQLiteBean::class.java)
            .where(SecretSQLiteBeanDao.Properties.Category.eq(category))
            .list()
        weakReferenceHandler.sendEmptyMessage(20210606)
    }

    override fun initEvent() {
        val snackBar = Snackbar.make(linearLayout, "长按账号和密码复制哦~", Snackbar.LENGTH_LONG)
        snackBar.setAction("知道了") { snackBar.dismiss() }
        snackBar.show()

        //List删除事件
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
                    QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("温馨提示")
                        .setMessage("删除后将无法恢复，是否继续？")
                        .setCanceledOnTouchOutside(false)
                        .addAction("容我想想") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .addAction("已经想好") { dialog, _ ->
                            dialog.dismiss()
                            daoSession.delete(secretBean)
                            secretList.removeAt(position)
                            isUpdate = true
                            weakReferenceHandler.sendEmptyMessage(20210606)
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

    private class WeakReferenceHandler(activity: SecretDetailActivity) : Handler() {
        private val mActivity: WeakReference<SecretDetailActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()
            when (msg.what) {
                20210606 -> {
                    if (activity!!.secretList.size == 0) {
                        activity.secretListView.visibility = View.GONE
                        activity.emptyLayout.visibility = View.VISIBLE
                    } else {
                        activity.secretListView.visibility = View.VISIBLE
                        activity.emptyLayout.visibility = View.GONE
                    }
                    if (activity.isUpdate) {
                        activity.secretListAdapter.notifyDataSetChanged()
                    } else {
                        activity.secretListAdapter =
                            SecretDetailAdapter(activity, activity.secretList)
                        activity.secretListView.adapter = activity.secretListAdapter
                        activity.secretListAdapter.setOnItemClickListener(object :
                            SecretDetailAdapter.OnChildViewClickListener {
                            override fun onAccountLongPressed(index: Int) {
                                val secretBean = activity.secretList[index]
                                val cipData =
                                    ClipData.newPlainText("secretAccount", secretBean.account)
                                activity.clipboardManager.setPrimaryClip(cipData)
                                ToastHelper.showToast("账号复制成功", ToastHelper.SUCCESS)
                            }

                            override fun onPasswordLongPressed(index: Int) {
                                val secretBean = activity.secretList[index]
                                val cipData =
                                    ClipData.newPlainText("secretPassword", secretBean.password)
                                activity.clipboardManager.setPrimaryClip(cipData)
                                ToastHelper.showToast("密码复制成功", ToastHelper.SUCCESS)
                            }

                            override fun onShareViewClicked(index: Int) {
                                val secretBean = activity.secretList[index]
                                val data = secretBean.account + "\r\n" + secretBean.password
                                val createCodeBitmap = QRUtils.getInstance().createQRCode(
                                    data,
                                    QMUIDisplayHelper.dp2px(activity, 300),
                                    QMUIDisplayHelper.dp2px(activity, 300)
                                )
                                ShareDialog.Builder().setContext(activity)
                                    .setDialogTitle("请不要将此二维码随意泄露给他人")
                                    .setDialogBitmap(createCodeBitmap).build().show()
                            }

                            override fun onModifyViewClicked(index: Int) {
                                InputDialogPlus.Builder().setContext(activity)
                                    .setCategory(activity.category)
                                    .setOnDialogClickListener(object :
                                        InputDialogPlus.DialogClickListener {
                                        override fun onConfirmClicked(
                                            category: String,
                                            password: String,
                                            remarks: String
                                        ) {
                                            if (password == "") {
                                                ToastHelper.showToast(
                                                    "不能将密码修改为空值",
                                                    ToastHelper.WARING
                                                )
                                                return
                                            }
                                            activity.daoSession.update(activity.secretList[index])
                                            activity.finish()
                                        }
                                    }).build()
                                    .show(activity.supportFragmentManager, "modifyPassword")
                            }
                        })
                    }
                }
            }
        }
    }
}