package com.pengxh.secretkey.widgets.gesture

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.ColorHelper

class GestureLockView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) :
    View(ctx, attrs), ILockView {

    private val mContext = ctx
    private val mPaint: Paint = Paint()
    private var mCurrentState: Int = ILockView.NO_FINGER
    private var mOuterRadius = 0f
    private var mInnerRadius = 0f

    init {
        mPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        width = width.coerceAtMost(height)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val space = 10f
        val x = width / 2.toFloat()
        val y = height / 2.toFloat()
        canvas.translate(x, y)
        mOuterRadius = (x - space) * 4 / 5 //外圆半径
        mInnerRadius = (x - space) / 3 //内圆半径
        when (mCurrentState) {
            ILockView.NO_FINGER -> drawNoFinger(canvas)
            ILockView.FINGER_TOUCH -> drawFingerTouch(canvas)
            ILockView.FINGER_UP_MATCHED -> drawFingerUpMatched(canvas)
            ILockView.FINGER_UP_UN_MATCHED -> drawFingerUpUnmatched(canvas)
        }
    }

    /**
     * 画无手指触摸状态
     *
     * @param canvas
     */
    private fun drawNoFinger(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = ColorHelper.getXmlColor(mContext, R.color.lightGray)
        mPaint.strokeWidth = DensityUtil.dp2px(context, 1f).toFloat()
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)
    }

    /**
     * 画手指触摸状态
     *
     * @param canvas
     */
    private fun drawFingerTouch(canvas: Canvas) {
        //内部填充色
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#ADD5E6")
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)

        //内部圆点填充色
        mPaint.color = ColorHelper.getXmlColor(mContext, R.color.mainThemeColor)
        canvas.drawCircle(0f, 0f, mInnerRadius, mPaint)

        //外部连线
        mPaint.style = Paint.Style.STROKE
        mPaint.color = ColorHelper.getXmlColor(mContext, R.color.mainThemeColor)
        mPaint.strokeWidth = DensityUtil.dp2px(context, 1f).toFloat()
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)
    }

    /**
     * 画手指抬起，匹配状态
     *
     * @param canvas
     */
    private fun drawFingerUpMatched(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#ADD5E6")
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)
        mPaint.color = ColorHelper.getXmlColor(mContext, R.color.mainThemeColor)
        canvas.drawCircle(0f, 0f, mInnerRadius, mPaint)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = ColorHelper.getXmlColor(mContext, R.color.mainThemeColor)
        mPaint.strokeWidth = DensityUtil.dp2px(context, 1f).toFloat()
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)
    }

    /**
     * 画手指抬起，不匹配状态
     *
     * @param canvas
     */
    private fun drawFingerUpUnmatched(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#EDACA7") //密码错误时园内填充色
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)
        mPaint.color = Color.RED
        canvas.drawCircle(0f, 0f, mInnerRadius, mPaint)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.RED
        mPaint.strokeWidth = DensityUtil.dp2px(context, 1f).toFloat()
        canvas.drawCircle(0f, 0f, mOuterRadius, mPaint)
    }

    override val view: View
        get() = this

    override fun onNoFinger() {
        mCurrentState = ILockView.NO_FINGER
        postInvalidate()
    }

    override fun onFingerTouch() {
        mCurrentState = ILockView.FINGER_TOUCH
        postInvalidate()
    }

    override fun onFingerUpMatched() {
        mCurrentState = ILockView.FINGER_UP_MATCHED
        postInvalidate()
    }

    override fun onFingerUpUnmatched() {
        mCurrentState = ILockView.FINGER_UP_UN_MATCHED
        postInvalidate()
    }

    fun setCurrentState(state: Int) {
        mCurrentState = state
        postInvalidate()
    }
}