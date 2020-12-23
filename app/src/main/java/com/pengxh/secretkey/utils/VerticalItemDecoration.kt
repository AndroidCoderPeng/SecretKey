package com.pengxh.secretkey.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.utils.callback.DecorationCallback
import java.util.*

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/9/27 22:47
 */
class VerticalItemDecoration(
    private val context: Context,
    private val callback: DecorationCallback
) : ItemDecoration() {
    private val topLinePaint: Paint = Paint()
    private val bottomLinePaint: Paint
    private val textPaint: TextPaint
    private val topGap: Int

    init {
        topLinePaint.isAntiAlias = true
        topLinePaint.color = Color.parseColor("#F1F1F1")

        bottomLinePaint = Paint()
        bottomLinePaint.isAntiAlias = true
        bottomLinePaint.color = Color.LTGRAY

        textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = sp2px(context, 18f).toFloat()
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.LEFT

        topGap = DensityUtil.dp2px(context, 30f)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        val groupTag = callback.getGroupTag(pos)
        if (groupTag < 0) return
        if (pos == 0 || isFirstInGroup(pos)) { //同组的第一个才添加padding
            outRect.top = topGap
        } else {
            outRect.top = 0
        }
    }

    /**
     * 判断是否为同组数据
     */
    private fun isFirstInGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            val prevGroupId = callback.getGroupTag(pos - 1)
            val groupId = callback.getGroupTag(pos)
            prevGroupId != groupId
        }
    }

    //画分割线
    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            c.drawRect(
                DensityUtil.dp2px(context, 15f).toFloat(),
                view.bottom.toFloat(),
                parent.width.toFloat(),
                view.bottom + 1.toFloat(),
                bottomLinePaint
            )
        }
    }

    //吸顶效果
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDrawOver(c, parent, state)
        val itemCount = state.itemCount
        val childCount = parent.childCount
        val left = parent.paddingLeft + DensityUtil.dp2px(context, 15f)
        val right = parent.width
        var preGroupId: Long
        var groupId: Long = -1
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            preGroupId = groupId
            groupId = callback.getGroupTag(position)
            if (groupId < 0 || groupId == preGroupId) continue
            val firstLetter = callback.getGroupFirstLetter(position).toUpperCase(Locale.ROOT)
            if (TextUtils.isEmpty(firstLetter)) continue
            val viewBottom = view.bottom
            var textY = Math.max(topGap, view.top).toFloat()
            if (position + 1 < itemCount) { //下一个和当前不一样移动当前
                val nextGroupId = callback.getGroupTag(position + 1)
                if (nextGroupId != groupId && viewBottom < textY) { //组内最后一个view进入了header
                    textY = viewBottom.toFloat()
                }
            }
            c.drawRect(0f, textY - topGap, right.toFloat(), textY, topLinePaint)
            c.drawText(
                firstLetter,
                left.toFloat(),
                textY - DensityUtil.dp2px(context, 7f),
                textPaint
            )
        }
    }

    /**
     * 点击某个字母将RecyclerView滑动到item顶部
     */
    class TopSmoothScroller(context: Context?) :
        LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    /**
     * sp转换成px
     */
    private fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}