package com.pengxh.secretkey.widgets.gesture

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.pengxh.secretkey.R
import java.util.*

class GestureLockDisplayView @JvmOverloads constructor(context: Context?,
    attrs: AttributeSet? = null) : View(context, attrs) {
    private val mPaint: Paint = Paint()
    private var mDotCount = 3

    // n * mCircleRadius*2 + ( n + 1 ) * mDotMargin = getWidth();
    private var mCircleRadius = 0

    //mCircle*0.5
    private var mDotMargin = 0

    //选中颜色
    private var mDotSelectedColor = resources.getColor(R.color.mainThemeColor)

    //未选中时颜色
    private var mDotUnSelectedColor = resources.getColor(R.color.lightGray)
    private val mDotList: MutableList<Dot> = ArrayList(1)
    private var mAnswerList: MutableList<Int> = ArrayList(1)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        width = width.coerceAtMost(height)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCircleRadius = (width * 2 * 1.0f / (5 * mDotCount + 1)).toInt()
        mDotMargin = (0.8 * mCircleRadius).toInt()
        for (i in 0 until mDotCount * mDotCount) {
            //计算圆心坐标
            val x =
                i % mDotCount * 2 * mCircleRadius + mCircleRadius + i % mDotCount * mDotMargin + mDotMargin.toFloat()
            val y =
                i / mDotCount * 2 * mCircleRadius + mCircleRadius + i / mDotCount * mDotMargin + mDotMargin.toFloat()
            //初始化点坐标
            mDotList.add(Dot(x, y, i))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //同步dot状态
        syncAnswerState()
        for (i in mDotList.indices) {
            val dot = mDotList[i]
            if (dot.isSelected) {
                mPaint.color = mDotSelectedColor
            } else {
                mPaint.color = mDotUnSelectedColor
            }
            canvas.drawCircle(dot.x, dot.y, mCircleRadius.toFloat(), mPaint)
        }
    }

    private fun syncAnswerState() {
        //先重置所有点的状态
        for (dot in mDotList) {
            dot.isSelected = false
        }
        //设置答案index的状态
        for (i in mAnswerList.indices) {
            val index = mAnswerList[i]
            mDotList[index].isSelected = true
        }
    }

    /**
     * 设置答案
     *
     * @param list
     */
    fun setAnswer(list: MutableList<Int>) {
        mAnswerList = list
        postInvalidate()
    }

    /**
     * 设置选中颜色
     *
     * @param color
     */
    fun setDotSelectedColor(color: Int) {
        mDotSelectedColor = color
        postInvalidate()
    }

    /**
     * 设置非选中颜色
     *
     * @param color
     */
    fun setDotUnSelectedColor(color: Int) {
        mDotUnSelectedColor = color
        postInvalidate()
    }

    /**
     * 设置点的个数
     *
     * @param dotCount
     */
    fun setDotCount(dotCount: Int) {
        mDotCount = dotCount
    }

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.BLACK //默认颜色
    }
}