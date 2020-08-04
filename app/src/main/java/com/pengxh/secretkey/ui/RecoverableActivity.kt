package com.pengxh.secretkey.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.aihook.alertview.library.AlertView
import com.aihook.alertview.library.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.RecoverableAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import kotlinx.android.synthetic.main.activity_recoverable.*
import kotlinx.android.synthetic.main.include_title_white.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/8/4 15:43
 */
class RecoverableActivity : BaseActivity() {

    companion object {
        private const val Tag = "RecoverableActivity"
    }

    private lateinit var loadRecoverableData: MutableList<SecretBean>

    override fun initLayoutView(): Int = R.layout.activity_recoverable

    override fun initData() {
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()

        mTitleView.text = "数据恢复"
    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        loadRecoverableData = SQLiteUtil(this).loadRecoverableData()
        recoverableSize.text = "共" + loadRecoverableData.size + "条数据可被恢复"
        initUI(loadRecoverableData)
    }

    override fun onResume() {
        super.onResume()
        //侧滑菜单
        recoverableListView.adapter = RecoverableAdapter(this, loadRecoverableData)
        recoverableListView.setMenuCreator { menu ->
            val recoverItem = SwipeMenuItem(this)
            recoverItem.setIcon(R.drawable.ic_recover)
            recoverItem.background =
                ColorDrawable(ColorHelper.getXmlColor(this, R.color.colorAccent))
            recoverItem.width = DensityUtil.dp2px(this, 70.0f)
            recoverItem.title = "恢复"
            recoverItem.titleSize = 18
            recoverItem.titleColor = Color.WHITE
            menu.addMenuItem(recoverItem)

            val deleteItem = SwipeMenuItem(this)
            deleteItem.setIcon(R.drawable.ic_delete_white)
            deleteItem.background = ColorDrawable(Color.rgb(251, 81, 81))
            deleteItem.width = DensityUtil.dp2px(this, 70.0f)
            deleteItem.title = "删除"
            deleteItem.titleSize = 18
            deleteItem.titleColor = Color.WHITE
            menu.addMenuItem(deleteItem)
        }
        recoverableListView.setOnMenuItemClickListener { position, menu, index ->
            when (index) {
                0 -> AlertView("温馨提示",
                    "是否恢复此条数据",
                    "取消",
                    arrayOf("确定"),
                    null,
                    this,
                    AlertView.Style.Alert,
                    OnItemClickListener { o: Any?, i: Int ->
                        if (i == 0) {

                        }
                    }).setCancelable(false).show()
                1 -> AlertView("温馨提示", "此次删除后将无法恢复，是否继续？",
                    "容我想想",
                    arrayOf("已经想好"),
                    null,
                    this,
                    AlertView.Style.Alert,
                    OnItemClickListener { o: Any?, i: Int ->
                        if (i == 0) {

                        }
                    }).setCancelable(false).show()
            }
            false
        }
    }

    private fun initUI(list: MutableList<SecretBean>) {
        if (list.size > 0) {
            recoverableLayout.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.VISIBLE
            recoverableLayout.visibility = View.GONE
        }
    }
}