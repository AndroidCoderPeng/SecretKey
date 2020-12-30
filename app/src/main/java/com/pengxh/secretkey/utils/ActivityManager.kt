package com.pengxh.secretkey.utils

import android.app.Activity
import android.util.Log


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 页面管理工具
 * @date: 2020年12月30日19:26:32
 */
class ActivityManager private constructor() {
    companion object {
        private const val Tag = "ActivityManager"
        val manager: ActivityManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }

    private val activityMap: HashMap<String, Activity> = HashMap()

    //添加activity
    fun addActivity(activity: Activity) {
        Log.d(Tag, "添加: ${activity.localClassName}")
        activityMap[activity.localClassName] = activity
    }

    //移除activity
    fun removeActivity(key: String) {
        Log.d(Tag, "关闭: $key")
        activityMap[key]?.finish()
        activityMap.remove(key)
    }
}