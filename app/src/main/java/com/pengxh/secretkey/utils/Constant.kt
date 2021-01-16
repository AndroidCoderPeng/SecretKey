package com.pengxh.secretkey.utils

import com.pengxh.secretkey.R

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/1 21:57
 */
object Constant {
    val IMAGES = arrayOf(
        R.drawable.ic_web,
        R.drawable.ic_app,
        R.drawable.ic_game,
        R.drawable.ic_card,
        R.drawable.ic_work,
        R.drawable.ic_email,
        R.drawable.ic_chat,
        R.mipmap.other
    )
    val CATEGORY = arrayOf("网站", "APP", "游戏", "银行卡", "工作", "邮箱", "聊天", "其他")

    const val PASSWORD_MODE = "mode"

    val excelTitle = arrayOf("类别", "标题", "账号", "密码", "备注")

    const val ACTION_UPDATE = "action_update"
}