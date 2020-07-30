package com.pengxh.secretkey.bean

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/7/24 14:48
 */
class SecretBean {
    lateinit var secretCategory: String //密码所在的分类

    lateinit var secretTitle: String //密码所对应的站名或者应用名

    lateinit var secretAccount: String //账号

    lateinit var secretPassword: String //密码

    lateinit var recoverable: String //是否可以恢复  0-可恢复，1-不可恢复
}