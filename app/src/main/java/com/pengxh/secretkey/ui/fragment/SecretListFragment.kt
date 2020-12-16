package com.pengxh.secretkey.ui.fragment

import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.ColorHelper
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.StatusBarColorUtil
import com.pengxh.secretkey.widgets.SlideBarView
import kotlinx.android.synthetic.main.fragment_secretlist.*

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

    private val letters: MutableList<String> = ArrayList()
//    private var secretAdapter: SecretListAdapter? = null
//    private var dataBeans: ArrayList<SecretTagBean> = ArrayList()

    override fun initLayoutView(): Int = R.layout.fragment_secretlist

    override fun initData() {
        activity?.let {
            StatusBarColorUtil.setColor(it, ColorHelper.getXmlColor(it, R.color.colorAccent))
        }
        ImmersionBar.with(this).init()
        settingsTitle.text = "密码列表"
    }

    override fun initEvent() {
//        secretAdapter = SecretListAdapter(context)
    }

    //需要及时的刷新界面
    override fun onResume() {
        super.onResume()
        val allSecret = context?.let { SQLiteUtil(it).loadAllSecret() }
        if (!allSecret.isNullOrEmpty()) {
            emptyLayout.visibility = View.GONE
            dataLayout.visibility = View.VISIBLE

            allSecret.forEach {
                letters.add(it.secretTitle!!)
            }
            initPopupWindow()
        } else {
            emptyLayout.visibility = View.VISIBLE
            dataLayout.visibility = View.GONE
        }
    }

    private fun initPopupWindow() {
        val layoutInflater = LayoutInflater.from(context)
        val rootView: View = layoutInflater.inflate(R.layout.fragment_secretlist, null)
        val contentView: View = layoutInflater.inflate(R.layout.layout_popup, null)
        val popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )
        popupWindow.contentView = contentView
        val letterView = contentView.findViewById<TextView>(R.id.letterView)
        val countDownTimer: CountDownTimer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                popupWindow.dismiss()
            }
        }
        slideBarView.setData(letters)
        slideBarView.setOnIndexChangeListener(object : SlideBarView.OnIndexChangeListener {
            override fun onIndexChange(letter: String?) {
                //在屏幕中间放大显示被按到的字母
                letterView.text = letter
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)
                countDownTimer.start()
                //根据滑动显示的字母索引到城市名字第一个汉字
                secretRecyclerView.smoothScrollToPosition(slideBarView.obtainFirstLetterIndex(letter!!))
            }
        })
    }

//    private fun initSecretList() {
//        Log.d(TAG, "initSecretList: " + Gson().toJson(dataBeans))
//        context?.let {
//            secretAdapter?.setData(dataBeans)
//            val layoutManager: LinearLayoutManager = object : LinearLayoutManager(it) {
//                override fun smoothScrollToPosition(
//                    recyclerView: RecyclerView,
//                    state: RecyclerView.State,
//                    position: Int
//                ) {
//                    val scroller = VerticalItemDecoration.TopSmoothScroller(it)
//                    scroller.targetPosition = position
//                    startSmoothScroll(scroller)
//                }
//            }
//            secretRecyclerView.layoutManager = layoutManager
//            secretRecyclerView.addItemDecoration(
//                VerticalItemDecoration(it,
//                    object : DecorationCallback {
//                        override fun getGroupTag(position: Int): Long {
//                            return dataBeans[position].tag!!.toCharArray()[0].toLong()
//                        }
//
//                        override fun getGroupFirstLetter(position: Int): String {
//                            return dataBeans[position].tag!!
//                        }
//                    })
//            )
//            secretRecyclerView.adapter = secretAdapter
//            secretAdapter?.setOnCityItemClickListener(object :
//                SecretListAdapter.OnCityItemClickListener {
//                override fun onClick(position: Int) {
//                    val secretTagBean = dataBeans[position]
//                    Log.d("SecretListFragment", "onClick: ${Gson().toJson(secretTagBean)}")
//
//                }
//            })
//        }
//    }

    /**
     * 将密码整理成分组数据，需要整理中英文情况
     */
//    private fun obtainSecretTagData(): ArrayList<SecretTagBean> {
//        //先将数据按照字母排序
//        for (i in 0 until letters.size) {
//            var title: String = letters[i]
//            //获取账号第一个字符
//            val first = title.substring(0, 1)
//            //判断首字符是否为中文，如果是中文便将首字符拼音的首字母和-符号加在字符串前面
//            val pattern = Pattern.compile("[\\u4e00-\\u9fa5]+")
//            if (pattern.matcher(first).matches()) {
//                title = obtainFirstHanYuPinyin(first).toString() + "-" + title
//                letters[i] = title
//            }
//        }
//        val comparator: Comparator<Any> = Collator.getInstance(Locale.CHINA)
//        Collections.sort<String>(letters, comparator)
//        //遍历数组，去除标识符-及首字母
//        for (i in 0 until letters.size) {
//            val str: String = letters[i]
//            if (str.contains("-") && str.indexOf("-") == 1) {
//                letters[i] = str.split("-".toRegex()).toTypedArray()[1]
//            }
//        }
//        //格式化数据
//
//        for (s in letters) {
//            val secretTagBean = SecretTagBean()
//            secretTagBean.tag = StringHelper.obtainHanYuPinyin(s).substring(0, 1).toUpperCase(
//                Locale.ROOT
//            )
//            secretTagBean.title = s
//            dataBeans.add(secretTagBean)
//        }
//        return dataBeans
//    }

    private fun obtainIndex(title: String): Int {
        return 0
    }
}