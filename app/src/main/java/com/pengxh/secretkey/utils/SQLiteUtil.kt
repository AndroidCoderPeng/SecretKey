package com.pengxh.secretkey.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pengxh.secretkey.bean.SecretBean
import java.util.*
import kotlin.properties.Delegates

class SQLiteUtil private constructor() {

    private val db: SQLiteDatabase

    init {
        val mSqLiteUtilHelper = SQLiteUtilHelper(context, DB_NAME, null, VERSION)
        db = mSqLiteUtilHelper.writableDatabase
    }

    companion object {
        private const val Tag = "SQLiteUtil"
        private var context: Context? = null

        /**
         * 数据库名
         */
        private const val DB_NAME = "SecretKey.db"

        /**
         * 数据库表名
         */
        private const val SECRET = "SecretTable"

        /**
         * 数据库版本
         */
        private const val VERSION = 1

        private var sqLiteUtil: SQLiteUtil by Delegates.notNull() //委托模式单例

        fun initDataBase(mContext: Context) {
            context = mContext.applicationContext
        }

        fun instance() = sqLiteUtil
    }

    fun saveSecret(secretCategory: String,
        secretTitle: String,
        secretAccount: String,
        secretPassword: String,
        recoverable: String) {
        if (!isSecretExist(secretTitle)) {
            //需要双重判断，title和account都相同才认为是同一个账号修改密码，否则认为是新增账号密码
            val values = ContentValues()
            values.put("secretCategory", secretCategory)
            values.put("secretTitle", secretTitle)
            values.put("secretAccount", secretAccount)
            values.put("secretPassword", secretPassword)
            values.put("recoverable", recoverable)
            db.insert(SECRET, null, values)
        } else {

        }
    }

    fun loadAllSecret(): List<SecretBean> {
        val list: MutableList<SecretBean> = ArrayList()
        val cursor = db.query(SECRET, null, null, null, null, null, "id DESC") //倒序
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val secretBean = SecretBean()
            secretBean.secretCategory = cursor.getString(cursor.getColumnIndex("secretCategory"))
            secretBean.secretTitle = cursor.getString(cursor.getColumnIndex("secretTitle"))
            secretBean.secretAccount = cursor.getString(cursor.getColumnIndex("secretAccount"))
            secretBean.secretPassword = cursor.getString(cursor.getColumnIndex("secretPassword"))
            secretBean.recoverable = cursor.getString(cursor.getColumnIndex("recoverable"))
            list.add(secretBean)
            //下一次循环开始
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    fun deleteAll() {
        db.delete(SECRET, null, null)
    }

    private fun isSecretExist(selectionArgs: String): Boolean {
        var result = false
        var cursor: Cursor? = null
        try {
            cursor =
                db.query(SECRET, null, "secretTitle = ?", arrayOf(selectionArgs), null, null, null)
            result = null != cursor && cursor.moveToFirst()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != cursor && !cursor.isClosed) {
                cursor.close()
            }
        }
        return result
    }
}