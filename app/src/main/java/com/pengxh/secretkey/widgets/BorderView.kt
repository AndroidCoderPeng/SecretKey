package com.pengxh.secretkey.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.pengxh.app.multilib.utils.DensityUtil

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/18 20:31
 */
class BorderView(context: Context, attrs: AttributeSet?) :
    AppCompatImageView(context, attrs) {

    private val mContext: Context = context
    private val borderPaint: Paint = Paint()
    private val textPaint: Paint = TextPaint()
    private var viewHeight = 0
    private var viewWidth = 0
    private var centerX = 0f
    private var centerY = 0f

    companion object {
        private const val TEXT = "请将银行卡置于方框内，便于识别卡号"
    }

    init {
        borderPaint.isAntiAlias = true
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 5f //设置线宽
        borderPaint.isAntiAlias = true
        borderPaint.alpha = 255

        //文字画笔
        textPaint.color = Color.GREEN
        textPaint.isAntiAlias = true
        textPaint.textSize = sp2px(context).toFloat()
        textPaint.alpha = 255
    }

    //计算出中心位置，便于定位
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //shr 相当于>>，位运算
        centerX = (w shr 1.toFloat().toInt()).toFloat()
        centerY = (h shr 1.toFloat().toInt()).toFloat()
    }

    //计算控件实际大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // 获取宽
        viewWidth = if (widthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            widthSpecSize
        } else {
            // wrap_content
            DensityUtil.dp2px(mContext, 300f)
        }
        // 获取高
        viewHeight = if (heightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            heightSpecSize
        } else {
            // wrap_content
            DensityUtil.dp2px(mContext, 180f)
        }
        // 设置该view的宽高
        setMeasuredDimension(viewWidth, viewHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制圆角矩形
        val borderRectF = RectF()
        borderRectF.left = 0f
        borderRectF.top = 0f
        borderRectF.right = viewWidth.toFloat()
        borderRectF.bottom = viewHeight.toFloat()
        canvas.drawRoundRect(borderRectF, 25f, 25f, borderPaint) //第二个参数是x半径，第三个参数是y半径

        //绘制文字
        val textRect = Rect()
        textPaint.getTextBounds(
            TEXT,
            0,
            TEXT.length,
            textRect
        )
        val textWidth = textRect.width()
        val textHeight = textRect.height()
        //计算文字左下角坐标
        val textX = centerX - (textWidth shr 1)
        val textY = centerY + (textHeight shr 1)
        canvas.drawText(TEXT, textX, textY, textPaint)
    }

    /**
     * sp转换成px
     */
    private fun sp2px(context: Context): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (16.toFloat() * fontScale + 0.5f).toInt()
    }
}