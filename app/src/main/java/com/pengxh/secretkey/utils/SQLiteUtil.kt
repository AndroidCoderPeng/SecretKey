package com.pengxh.secretkey.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.pengxh.secretkey.bean.SecretBean
import java.util.*

class SQLiteUtil(mContext: Context) {

    companion object {
        private const val Tag = "SQLiteUtil"
    }

    private val db: SQLiteDatabase
    private var context: Context = mContext

    /**
     * 数据库名
     */
    private val databaseName = "SecretKey.db"

    /**
     * 数据库表名
     */
    private val tableName = "SecretTable"

    /**
     * 数据库版本
     */
    private val databaseVersion = 1

    init {
        val mSqLiteUtilHelper = SQLiteUtilHelper(context, databaseName, null, databaseVersion)
        db = mSqLiteUtilHelper.writableDatabase
    }

    /**
     * 保存账号密码
     * */
    fun saveSecret(secretCategory: String,
        secretTitle: String,
        secretAccount: String,
        secretPassword: String,
        recoverable: String) {
        if (!isSecretExist(secretTitle, secretAccount)) {
            val values = ContentValues()
            values.put("secretCategory", secretCategory)
            values.put("secretTitle", secretTitle)
            values.put("secretAccount", secretAccount)
            values.put("secretPassword", secretPassword)
            values.put("recoverable", recoverable)
            db.insert(tableName, null, values)
        } else {
            Log.d(Tag, "『$secretTitle』已存在")
        }
    }

    /**
     * 加载某个分类的数据
     * */
    fun loadCategory(category: String): List<SecretBean> {
        val list: MutableList<SecretBean> = ArrayList()
        val cursor = db.query(tableName,
            null,
            "secretCategory = ?",
            arrayOf(category),
            null,
            null,
            "id DESC") //倒序
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val resultBean = SecretBean()
            resultBean.secretCategory = cursor.getString(cursor.getColumnIndex("secretCategory"))
            resultBean.secretTitle = cursor.getString(cursor.getColumnIndex("secretTitle"))
            resultBean.secretAccount = cursor.getString(cursor.getColumnIndex("secretAccount"))
            resultBean.secretPassword = cursor.getString(cursor.getColumnIndex("secretPassword"))
            resultBean.recoverable = cursor.getString(cursor.getColumnIndex("recoverable"))
            list.add(resultBean)
            //下一次循环开始
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    fun deleteAll() {
        db.delete(tableName, null, null)
    }

    /**
     * 双重判断，title和account都相同才认为是同一个账号修改密码，否则认为是新增账号密码
     * */
    private fun isSecretExist(title: String, account: String): Boolean {
        var result = false
        var cursor: Cursor? = null
        try {
            cursor = db.query(tableName,
                null,
                "secretTitle = ? and secretAccount = ?",
                arrayOf(title, account),
                null,
                null,
                null)
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