package com.pengxh.secretkey.utils

abstract class SecretComparator<T> : Comparator<T> {

    //重写比较方法
    abstract override fun compare(left: T, right: T): Int
}