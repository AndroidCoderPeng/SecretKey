package com.pengxh.secretkey.ui.fragment

import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pengxh.app.multilib.base.BaseFragment
import com.pengxh.secretkey.R
import com.pengxh.secretkey.adapter.SecretListAdapter
import com.pengxh.secretkey.bean.SecretBean
import com.pengxh.secretkey.bean.SecretTagBean
import com.pengxh.secretkey.utils.SQLiteUtil
import com.pengxh.secretkey.utils.SecretComparator
import com.pengxh.secretkey.utils.StringHelper
import com.pengxh.secretkey.utils.VerticalItemDecoration
import com.pengxh.secretkey.utils.callback.DecorationCallback
import com.pengxh.secretkey.widgets.SlideBarView
import kotlinx.android.synthetic.main.fragment_secretlist.*
import org.jetbrains.annotations.Nullable
import java.text.Collator
import java.util.*
import java.util.regex.Pattern
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
    private var secretAdapter: SecretListAdapter? = null

    override fun initLayoutView(): Int = R.layout.fragment_secretlist

    override fun initData() {
        settingsTitle.text = "密码列表"
        sqLiteUtil = SQLiteUtil()
    }

    override fun initEvent() {
        dataBeans = obtainSecretTagData()
        if (!dataBeans.isNullOrEmpty()) {
            emptyLayout.visibility = View.GONE
            dataLayout.visibility = View.VISIBLE

            initSecretList()
        } else {
            emptyLayout.visibility = View.VISIBLE
            dataLayout.visibility = View.GONE
        }
    }

    private fun initSecretList() {
        secretAdapter = SecretListAdapter(context, dataBeans)
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

            }
        })
    }

    private fun initPopupWindow(letters: ArrayList<String>, allData: ArrayList<SecretBean>) {
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
        //将源数据和侧边栏数据都传递给侧边栏View，方便定位index
        slideBarView.setData(letters, allData)
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

    /**
     * 将密码整理成分组数据，需要整理中英文情况
     */
    private fun obtainSecretTagData(): ArrayList<SecretTagBean> {
        val allSecret = sqLiteUtil.loadAllSecret()
        val list = ArrayList<SecretTagBean>()
        val slideBarLetters = ArrayList<String>()
        if (!allSecret.isNullOrEmpty()) {
            //将中英文重新混合排序
            for (resultBean in allSecret) {
                var secretTitle = resultBean.secretTitle
                //获取账号第一个字符
                val first = secretTitle!!.substring(0, 1)
                val pattern: Pattern = Pattern.compile("[\\u4e00-\\u9fa5]+")
                if (pattern.matcher(first).matches()) {
                    secretTitle =
                        StringHelper.obtainFirstHanYuPinyin(first).toString() + "-" + secretTitle
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
            Collections.sort(allSecret, secretComparator)
            //获取侧边栏title
            allSecret.forEach {
                slideBarLetters.add(it.secretTitle!!)
            }
            //将中文转化为大写字母
            val letterSet = HashSet<String>()
            for (s in slideBarLetters) {
                val firstLetter =
                    StringHelper.obtainHanYuPinyin(s).substring(0, 1)
                        .toUpperCase(Locale.ROOT) //取每个title的首字母
                letterSet.add(firstLetter)
            }
            initPopupWindow(ArrayList(letterSet), allSecret)
            //去掉之前添加得标记
            for (bean in allSecret) {
                val secretTitle = bean.secretTitle
                if (secretTitle!!.contains("-") && secretTitle.indexOf("-") == 1) {
                    bean.secretTitle = secretTitle.substring(2)
                }
            }
            //获取列表数据
            for (bean in allSecret) {
                val secretTagBean = SecretTagBean()
                val s = bean.secretTitle!!
                secretTagBean.tag =
                    StringHelper.obtainHanYuPinyin(s).substring(0, 1).toUpperCase(
                        Locale.ROOT
                    )
                secretTagBean.title = s
                secretTagBean.account = bean.secretAccount
                list.add(secretTagBean)
            }
        }
        return list
    }
}