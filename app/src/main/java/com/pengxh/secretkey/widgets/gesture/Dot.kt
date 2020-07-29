package com.pengxh.secretkey.widgets.gesture

import java.io.Serializable

class Dot internal constructor( //圆心坐标
    var x: Float, var y: Float, //下标
    var index: Int) : Serializable {

    //当前状态,是否选中
    var isSelected = false
}