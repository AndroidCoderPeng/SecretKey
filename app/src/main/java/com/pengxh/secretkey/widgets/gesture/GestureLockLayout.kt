package com.pengxh.secretkey.widgets.gesture

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.R
import java.util.*

class GestureLockLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    RelativeLayout(context, attrs) {
    // 关于LockView的边长（n*n）： n * mLockViewWidth + ( n + 1 ) * mLockViewMargin = mWidth
    private var mLockViewWidth = 0

    //mLockViewWidth * 0.25
    private var mLockViewMargin = 0

    //LockView数组
    private val mILockViews: ArrayList<ILockView?>? = ArrayList(1)
    private var mLockViewFactory: LockViewFactory? = null

    //x*x的手势解锁
    private var mDotCount = 3

    //画笔
    private var mPaint: Paint? = null

    //路径
    private var mPath: Path? = null

    //连接线的宽度
    private val mStrokeWidth = 2f

    //手指触摸时，path颜色
    private var mFingerTouchColor = resources.getColor(R.color.mainThemeColor)

    //手指抬起时,密码匹配path颜色
    private var mFingerUpMatchedColor = resources.getColor(R.color.mainThemeColor)

    //手指抬起时,密码不匹配path颜色
    private var mFingerUpUnmatchedColor = Color.RED

    //path上一次moveTo到的点坐标
    private var mLastPathX = 0f
    private var mLastPathY = 0f

    //指引线的终点坐标
    private var mLineX = 0f
    private var mLineY = 0f

    //保存选中的LockView id
    private val mChooseList: ArrayList<Int>? = ArrayList(1)

    //答案list
    private val mAnswerList = ArrayList<Int>(1)

    //是否可以触摸
    private var mTouchable = true

    //允许的尝试次数
    private var mTryTimes = 5

    //保存的尝试次数，因为模式切换的时候TryTimes可能不等于初始设置的值
    private var mSavedTryTimes = 5
    private var mOnLockVerifyListener: OnLockVerifyListener? = null
    private var mOnLockResetListener: OnLockResetListener? = null

    //当前模式
    private var mCurrentMode = RESET_MODE

    //RESET_MODE下最少连接数
    private var mMinCount = 3

    companion object {
        //模式选择，重置密码，设置密码模式
        const val RESET_MODE = 0

        //验证密码模式
        const val VERIFY_MODE = 1
    }

    init {
        init(context)
    }

    private fun init(context: Context) {
        if (mLockViewFactory == null) {
            setLockView(object : LockViewFactory {
                override fun newLockView(): ILockView {
                    return GestureLockView(context)
                }
            })
        }
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = DensityUtil.dp2px(context, mStrokeWidth).toFloat()
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.strokeJoin = Paint.Join.ROUND
        mPath = Path()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        width = width.coerceAtMost(height)
        mLockViewWidth = (4 * width * 1.0f / (5 * mDotCount + 1)).toInt()
        //计算LockView的间距
        mLockViewMargin = (mLockViewWidth * 0.25).toInt()
        if (mLockViewFactory != null) {
            setLockViewParams(mLockViewFactory!!)
        }
    }

    /**
     * 设置LockView的参数并添加到布局中
     *
     * @param lockViewFactory
     */
    private fun setLockViewParams(lockViewFactory: LockViewFactory) {
        if (mILockViews!!.size > 0) {
            return
        }
        for (i in 0 until mDotCount * mDotCount) {
            val iLockView = lockViewFactory.newLockView()
            iLockView.view.id = i + 1
            mILockViews.add(iLockView)
            val lockerParams = LayoutParams(mLockViewWidth, mLockViewWidth)

            //不是每行的第一个，则设置位置为前一个的右边
            if (i % mDotCount != 0) {
                lockerParams.addRule(RIGHT_OF, mILockViews[i - 1]!!.view.id)
            }
            //从第二行开始，设置为上一行同一位置View的下面
            if (i > mDotCount - 1) {
                lockerParams.addRule(BELOW, mILockViews[i - mDotCount]!!.view.id)
            }

            //设置右下左上的边距
            val rightMargin = mLockViewMargin
            val bottomMargin = mLockViewMargin
            var leftMargin = 0
            var topMargin = 0

            //每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
            if (i in 0 until mDotCount) { //第一行
                topMargin = mLockViewMargin
            }
            if (i % mDotCount == 0) { //第一列
                leftMargin = mLockViewMargin
            }
            lockerParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
            mILockViews[i]!!.onNoFinger()
            mILockViews[i]!!.view.layoutParams = lockerParams
            addView(mILockViews[i]!!.view)
        }
    }

    /**
     * 保存状态
     *
     * @return
     */
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.tryTimes = mTryTimes
        return ss
    }

    /**
     * 恢复状态
     *
     * @param state
     */
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mTryTimes = savedState.tryTimes
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mTouchable) {
            val action = event.action
            val x = event.x.toInt()
            val y = event.y.toInt()
            when (action) {
                MotionEvent.ACTION_DOWN -> handleDownEvent(x, y)
                MotionEvent.ACTION_MOVE -> handleMoveEvent(x, y)
                MotionEvent.ACTION_UP -> handleUpEvent()
            }
            invalidate()
            true
        } else {
            false
        }
    }

    /**
     * 处理按下事件
     *
     * @param x
     * @param y
     */
    private fun handleDownEvent(x: Int, y: Int) {
        reset()
        handleMoveEvent(x, y)
    }

    /**
     * 处理移动事件
     *
     * @param x
     * @param y
     */
    private fun handleMoveEvent(x: Int, y: Int) {
        mPaint!!.color = mFingerTouchColor
        val lockView = getLockViewByPoint(x, y)
        if (lockView != null) {
            val childId = lockView.view.id
            if (!mChooseList!!.contains(childId)) {
                mChooseList.add(childId)
                lockView.onFingerTouch()

                //手势解锁监听
                if (mOnLockVerifyListener != null) {
                    mOnLockVerifyListener!!.onGestureSelected(childId)
                }
                mLastPathX = lockView.view.left / 2 + lockView.view.right / 2.toFloat()
                mLastPathY = lockView.view.top / 2 + lockView.view.bottom / 2.toFloat()
                if (mChooseList.size == 1) {
                    mPath!!.moveTo(mLastPathX, mLastPathY)
                } else {
                    mPath!!.lineTo(mLastPathX, mLastPathY)
                }
            }
        }
        //指引线终点坐标
        mLineX = x.toFloat()
        mLineY = y.toFloat()
    }

    /**
     * 处理抬起事件
     */
    private fun handleUpEvent() {
        if (mCurrentMode == RESET_MODE) {
            handleResetMode()
        } else {
            handleVerifyMode()
        }
        //将指引线的终点坐标设置为最后一个Path的原点，即取消指引线
        mLineX = mLastPathX
        mLineY = mLastPathY
    }

    /**
     * 处理修改密码模式
     */
    private fun handleResetMode() {
        if (mAnswerList.size <= 0) {
            //如果AnswerList.size()==0则为第一次设置，验证连接数
            if (mChooseList!!.size < mMinCount) {
                //连接数不符
                if (mOnLockResetListener != null) {
                    mOnLockResetListener!!.onConnectCountUnmatched(mChooseList.size, mMinCount)
                }
                toggleLockViewMatchedState(false)
                return
            } else {
                //连接数符合，将选择的答案赋值给mAnswerList
                for (integer in mChooseList) {
                    //因为mAnswerList是从0开始，chooseList保存的是id从1开始，所以-1
                    mAnswerList.add(integer - 1)
                }
                if (mOnLockResetListener != null) {
                    mOnLockResetListener!!.onFirstPasswordFinished(mAnswerList)
                }
                toggleLockViewMatchedState(true)
            }
        } else {
            //mAnswerList已有答案，则验证密码，两次密码匹配保存密码
            val isAnswerRight = checkAnswer()
            if (isAnswerRight) {
                //两次密码正确，回调
                toggleLockViewMatchedState(true)
                if (mOnLockResetListener != null) {
                    mOnLockResetListener!!.onSetPasswordFinished(true, mAnswerList)
                }
            } else {
                //两次没密码不正确
                toggleLockViewMatchedState(false)
                if (mOnLockResetListener != null) {
                    mOnLockResetListener!!.onSetPasswordFinished(false, ArrayList(1))
                }
            }
        }
    }

    /**
     * 处理验证密码模式
     */
    private fun handleVerifyMode() {
        mTryTimes--
        val isAnswerRight = checkAnswer()
        //手势解锁监听
        if (mOnLockVerifyListener != null) {
            mOnLockVerifyListener!!.onGestureFinished(isAnswerRight)
            if (mTryTimes <= 0) {
                mOnLockVerifyListener!!.onGestureTryTimesBoundary()
            }
        }
        if (!isAnswerRight) {
            toggleLockViewMatchedState(false)
        } else {
            toggleLockViewMatchedState(true)
        }
    }

    /**
     * 检查x,y点是否在LockView中
     *
     * @param childView
     * @param x
     * @param y
     * @return
     */
    private fun checkPointInChild(childView: View?, x: Int, y: Int): Boolean {
        //设置了内边距，即x,y必须落入下GestureLockView的内部中间的小区域中，可以通过调整padding使得x,y落入范围不变大，或者不设置padding
        val padding = (mLockViewWidth * 0.1).toInt()
        return x >= childView!!.left + padding && x <= childView.right - padding && y >= childView.top + padding && y <= childView.bottom - padding
    }

    /**
     * 同过x，y点获取LockView对象
     *
     * @param x
     * @param y
     * @return
     */
    private fun getLockViewByPoint(x: Int, y: Int): ILockView? {
        for (lockView in mILockViews!!) {
            if (checkPointInChild(lockView!!.view, x, y)) {
                return lockView
            }
        }
        return null
    }

    /**
     * 重置手势解锁
     */
    private fun reset() {
        if (mChooseList == null || mPath == null || mILockViews == null) {
            return
        }
        mChooseList.clear()
        mPath!!.reset()
        for (iLockView in mILockViews) {
            iLockView!!.onNoFinger()
        }
    }

    /**
     * 重置手势
     */
    fun resetGesture() {
        reset()
        invalidate()
    }

    /**
     * 检查答案是否正确
     *
     * @return
     */
    private fun checkAnswer(): Boolean {
        if (mAnswerList.size != mChooseList!!.size) {
            return false
        }
        for (i in mAnswerList.indices) {
            if (mAnswerList[i] != mChooseList[i] - 1) {
                return false
            }
        }
        return true
    }

    /**
     * 切换LockView是否匹配状态
     *
     * @param isMatched
     */
    private fun toggleLockViewMatchedState(isMatched: Boolean) {
        if (isMatched) {
            mPaint!!.color = mFingerUpMatchedColor
        } else {
            mPaint!!.color = mFingerUpUnmatchedColor
        }
        for (iLockView in mILockViews!!) {
            if (mChooseList!!.contains(iLockView!!.view.id)) {
                if (!isMatched) {
                    iLockView.onFingerUpUnmatched()
                } else {
                    iLockView.onFingerUpMatched()
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        //画Path
        canvas.drawPath(mPath!!, mPaint!!)
        //画指引线
        if (mChooseList!!.size > 0) {
            canvas.drawLine(mLastPathX, mLastPathY, mLineX, mLineY, mPaint!!)
        }
    }

    /**
     * 设置LockView
     *
     * @param lockViewFactory
     */
    private fun setLockView(lockViewFactory: LockViewFactory?) {
        if (lockViewFactory != null) {
            removeAllViewsInLayout()
            mILockViews!!.clear()
            mLockViewFactory = lockViewFactory
            if (mLockViewWidth > 0) {
                setLockViewParams(mLockViewFactory!!)
                reset()
            }
        }
    }

    /**
     * 将String类型的Answer设置到list
     * 必须时List的toString形式[x,x,x]
     *
     * @param str
     */
    fun setAnswer(str: String) {
        var answer = str
        if (answer.startsWith("[") && answer.endsWith("]")) {
            answer = answer.substring(1, answer.length - 1)
            val answers = answer.split(",").toTypedArray()
            mAnswerList.clear()
            for (i in answers.indices) {
                mAnswerList.add(answers[i].trim { it <= ' ' }.toInt())
            }
        }
    }

    /**
     * 设置是否可以触摸
     *
     * @param touchable
     */
    fun setTouchable(touchable: Boolean) {
        mTouchable = touchable
        reset()
        invalidate()
    }

    /**
     * 设置手势解锁监听器
     *
     * @param listener
     */
    fun setOnLockVerifyListener(listener: OnLockVerifyListener?) {
        mOnLockVerifyListener = listener
    }

    fun setOnLockResetListener(listener: OnLockResetListener?) {
        mOnLockResetListener = listener
    }

    /**
     * 设置路径宽度
     *
     * @param dp
     */
    fun setPathWidth(dp: Float) {
        mPaint!!.strokeWidth = DensityUtil.dp2px(context, dp).toFloat()
    }

    /**
     * 设置每行点的个数
     *
     * @param count
     */
    fun setDotCount(count: Int) {
        mDotCount = count
    }

    /**
     * 设置手指按下时Path颜色
     *
     * @param color
     */
    fun setTouchedPathColor(color: Int) {
        mFingerTouchColor = color
    }

    /**
     * 设置手指抬起时，密码匹配颜色
     *
     * @param color
     */
    fun setMatchedPathColor(color: Int) {
        mFingerUpMatchedColor = color
    }

    /**
     * 设置手指抬起时，密码不匹配的颜色
     *
     * @param color
     */
    fun setUnmatchedPathColor(color: Int) {
        mFingerUpUnmatchedColor = color
    }

    /**
     * 获取最大尝试次数
     *
     * @return
     */
    /**
     * 设置最大尝试次数
     *
     * @param tryTimes
     */
    var tryTimes: Int
        get() = mTryTimes
        set(tryTimes) {
            mTryTimes = tryTimes
            mSavedTryTimes = tryTimes
        }

    /**
     * 设置密码模式下，最小连接数
     *
     * @param minCount
     */
    fun setMinCount(minCount: Int) {
        mMinCount = minCount
    }

    fun setMode(mode: Int) {
        mCurrentMode = mode
        reset()
        //切换到验证模式的时候，还原最大尝试次数
        if (mCurrentMode == VERIFY_MODE) {
            mTryTimes = mSavedTryTimes
        } else if (mCurrentMode == RESET_MODE) {
            //清除已有密码数据
            mAnswerList.clear()
        }
    }

    interface OnLockVerifyListener {
        /**
         * 移动过程中选中的id
         *
         * @param id
         */
        fun onGestureSelected(id: Int)

        /**
         * 手势动作完成
         *
         * @param isMatched 是否和密码匹配
         */
        fun onGestureFinished(isMatched: Boolean)

        /**
         * 超过尝试次数上限
         */
        fun onGestureTryTimesBoundary()
    }

    interface OnLockResetListener {
        /**
         * 连接数不符
         *
         * @param connectCount
         * @param minCount
         */
        fun onConnectCountUnmatched(connectCount: Int, minCount: Int)

        /**
         * 连接数符合，第一次密码设置成功
         *
         * @param answerList
         */
        fun onFirstPasswordFinished(answerList: MutableList<Int>)

        /**
         * 设置密码成功
         *
         * @param isMatched  两次密码是否匹配
         * @param answerList 密码list
         */
        fun onSetPasswordFinished(isMatched: Boolean, answerList: MutableList<Int>)
    }

    /**
     * 保存状态bean
     */
    internal class SavedState : BaseSavedState {
        var tryTimes = 0

        constructor(source: Parcelable?) : super(source) {}

        /**
         * Constructor called from [.CREATOR]
         */
        private constructor(`in`: Parcel) : super(`in`) {
            tryTimes = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(tryTimes)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> =
                object : Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState? {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }
}