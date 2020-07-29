package com.pengxh.secretkey.widgets.gesture

import android.view.View

interface ILockView {
    /**
     * 获取View
     *
     * @return
     */
    val view: View

    /**
     * 手指没触摸之前，初始状态
     */
    fun onNoFinger()

    /**
     * 手指触摸，按下状态
     */
    fun onFingerTouch()

    /**
     * 手指抬起，手势密码匹配状态
     */
    fun onFingerUpMatched()

    /**
     * 手指抬起，手势密码不匹配状态
     */
    fun onFingerUpUnmatched()

    companion object {
        //手势状态
        const val NO_FINGER = 0
        const val FINGER_TOUCH = 1
        const val FINGER_UP_MATCHED = 2
        const val FINGER_UP_UN_MATCHED = 3
    }
}