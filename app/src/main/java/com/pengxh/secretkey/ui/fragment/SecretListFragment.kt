package com.pengxh.secretkey.ui.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.app.multilib.utils.BroadcastManager
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretListAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.bean.SecretTagBean
import com.pengxh.secretkey.utils.*
import com.pengxh.secretkey.utils.callback.DecorationCallback
import com.pengxh.secretkey.widgets.SecretDetailDialog
import com.pengxh.secretkey.widgets.SlideBarView
import kotlinx.android.synthetic.main.fragment_secretlist.*
import org.jetbrains.annotations.Nullable
import java.text.Collator
import java.util.*
import kotlin.collections.ArrayList


/**
 * @description:
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/1 22:37
 */
class SecretListFragment : BaseFragment() {

    companion object {
        private const val TAG = "SecretListFragment"
    }

    private lateinit var sqLiteUtil: SQLiteUtil
    private var dataBeans: ArrayList<SecretTagBean> = ArrayList()
    private var allSecretData: ArrayList<SecretBean> = ArrayList()
    private var secretAdapter: SecretListAdapter? = null
    private var isUpdateData = false

    override fun initLayoutView(): Int = R.layout.fragment_secretlist

    override fun initData() {
        settingsTitle.text = "密码列表"
        sqLiteUtil = SQLiteUtil()
    }

    override fun initEvent() {
        dataBeans = obtainSecretTagData()
        handler.sendEmptyMessage(1010)
        //监听是否导入了数据
        BroadcastManager.getInstance(context)
            .addAction(Constant.ACTION_UPDATE, object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val action = intent!!.action
                    if (action != null) {
                        val response = intent.getStringExtra("data")
                        if (response == "updateData") {
                            isUpdateData = true
                            dataBeans.clear()
                            dataBeans.addAll(obtainSecretTagData())
                            handler.sendEmptyMessage(1010)
                        }
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        if (!dataBeans.isNullOrEmpty()) {
            emptyLayout.visibility = View.GONE
            dataLayout.visibility = View.VISIBLE
        } else {
            emptyLayout.visibility = View.VISIBLE
            dataLayout.visibility = View.GONE
        }
    }

    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1010) {
                if (isUpdateData) {
                    secretAdapter?.notifyDataSetChanged()
                } else {
                    initSecretList()
                }
            }
        }
    }

    private fun initSecretList() {
        Log.d(TAG, "dataBeans: " + Gson().toJson(dataBeans))
        secretAdapter = SecretListAdapter(context, dataBeans, allSecretData)
        val layoutManager: LinearLayoutManager = object : LinearLayoutManager(context) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView,
                state: RecyclerView.State,
                position: Int
            ) {
                val scroller = VerticalItemDecoration.TopSmoothScroller(context)
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }
        secretRecyclerView.layoutManager = layoutManager
        secretRecyclerView.addItemDecoration(
            VerticalItemDecoration(context!!,
                object : DecorationCallback {
                    override fun getGroupTag(position: Int): Long {
                        return dataBeans[position].tag!!.toCharArray()[0].toLong()
                    }

                    override fun getGroupFirstLetter(position: Int): String {
                        return dataBeans[position].tag!!
                    }
                })
        )
        secretRecyclerView.adapter = secretAdapter
        secretAdapter?.setOnCityItemClickListener(object :
            SecretListAdapter.OnCityItemClickListener {
            override fun onClick(position: Int) {
                val secretTagBean = dataBeans[position]
                allSecretData.forEach {
                    if (it.secretTitle == secretTagBean.title && it.secretAccount == secretTagBean.account) {
                        SecretDetailDialog.Builder()
                            .setContext(context)
                            .setDialogTitle(it.secretTitle)
                            .setMessage(it.secretAccount)
                            .setSubMessage(it.secretPassword)
                            .setSecretMarks(it.secretRemarks)
                            .build().show()
                    }
                }
            }
        })
    }

    private fun initPopupWindow(letters: ArrayList<String>, allData: ArrayList<SecretBean>) {
        var layoutInflater: LayoutInflater? = null
        try {
            layoutInflater = LayoutInflater.from(context)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        val rootView: View? = layoutInflater?.inflate(R.layout.fragment_secretlist, null)
        val contentView: View? = layoutInflater?.inflate(R.layout.layout_popup, null)
        val popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )
        popupWindow.contentView = contentView
        val letterView = contentView?.findViewById<TextView>(R.id.letterView)
        val countDownTimer: CountDownTimer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                popupWindow.dismiss()
            }
        }
        //将源数据和侧边栏数据都传递给侧边栏View，方便定位index
        try {
            slideBarView.setData(letters, allData)
            slideBarView.setOnIndexChangeListener(object : SlideBarView.OnIndexChangeListener {
                override fun onIndexChange(letter: String?) {
                    //在屏幕中间放大显示被按到的字母
                    if (letterView != null) {
                        letterView.text = letter
                    }
                    popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)
                    countDownTimer.start()
                    //根据滑动显示的字母索引到城市名字第一个汉字
                    secretRecyclerView.smoothScrollToPosition(
                        slideBarView.obtainFirstLetterIndex(
                            letter!!
                        )
                    )
                }
            })
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    /**
     * 将密码整理成分组数据，需要整理中英文情况
     */
    private fun obtainSecretTagData(): ArrayList<SecretTagBean> {
        Log.d(TAG, "获取数据-开始")
        allSecretData = sqLiteUtil.loadAllSecret()
        val updateList = ArrayList<SecretTagBean>()
        val slideBarLetters = ArrayList<String>()
        if (!allSecretData.isNullOrEmpty()) {
            //将中英文重新混合排序
            for (resultBean in allSecretData) {
                var secretTitle = resultBean.secretTitle
                //获取账号第一个字符
                val first = secretTitle!!.substring(0, 1)
                if (StringHelper.isChinese(first) || StringHelper.isNumber(first)) {
                    secretTitle = StringHelper.obtainHanYuPinyin(first) + "-" + secretTitle
                    resultBean.secretTitle = secretTitle
                }
            }
            //准备排序
            val collator: Collator = Collator.getInstance(Locale.CHINA)
            val secretComparator: SecretComparator<SecretBean> =
                object : SecretComparator<SecretBean>() {
                    override fun compare(
                        @Nullable left: SecretBean,
                        @Nullable right: SecretBean
                    ): Int {
                        if (left.secretTitle == null) {
                            return -1
                        }
                        if (right.secretTitle == null) {
                            return 1
                        }
                        if (left.secretTitle == right.secretTitle) {
                            if (left.secretAccount == null) {
                                return -1
                            }
                            return if (right.secretAccount == null) {
                                1
                            } else collator.compare(left.secretAccount, right.secretAccount)
                        }
                        return collator.compare(left.secretTitle, right.secretTitle)
                    }
                }
            Collections.sort(allSecretData, secretComparator)
            //获取侧边栏title
            allSecretData.forEach {
                slideBarLetters.add(it.secretTitle!!)
            }
            //将中文转化为大写字母
            val letterSet = HashSet<String>()
            for (s in slideBarLetters) {
                val firstLetter =
                    StringHelper.obtainHanYuPinyin(s).toUpperCase(Locale.ROOT) //取每个title的首字母
                letterSet.add(firstLetter)
            }
            initPopupWindow(ArrayList(letterSet), allSecretData)
            //去掉之前添加得标记
            for (bean in allSecretData) {
                val secretTitle = bean.secretTitle
                if (secretTitle!!.contains("-") && secretTitle.indexOf("-") == 1) {
                    bean.secretTitle = secretTitle.substring(2)
                }
            }
            //获取列表数据
            for (bean in allSecretData) {
                val secretTagBean = SecretTagBean()
                val s = bean.secretTitle!!
                secretTagBean.tag = StringHelper.obtainHanYuPinyin(s).toUpperCase(Locale.ROOT)
                secretTagBean.title = s
                secretTagBean.account = bean.secretAccount
                updateList.add(secretTagBean)
            }
        }
        Log.d(TAG, "获取数据-结束" + Gson().toJson(updateList))
        return updateList
    }
}