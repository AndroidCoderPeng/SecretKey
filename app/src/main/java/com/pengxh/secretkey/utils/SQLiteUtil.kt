package com.pengxh.secretkey.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
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
            db.insert(tableName, null, values)
        } else {

        }
    }

    fun loadAllSecret(): List<SecretBean> {
        val list: MutableList<SecretBean> = ArrayList()
        val cursor = db.query(tableName, null, null, null, null, null, "id DESC") //倒序
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val secretBean = SecretBean()
            secretBean.category = cursor.getString(cursor.getColumnIndex("category"))

            val secret = SecretBean.Secret()
            secret.secretTitle = cursor.getString(cursor.getColumnIndex("secretTitle"))
            secret.secretAccount = cursor.getString(cursor.getColumnIndex("secretAccount"))
            secret.secretPassword = cursor.getString(cursor.getColumnIndex("secretPassword"))
            secret.recoverable = cursor.getString(cursor.getColumnIndex("recoverable"))
            secretBean.secret = listOf(secret)

            list.add(secretBean)
            //下一次循环开始
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    fun deleteAll() {
        db.delete(tableName, null, null)
    }

    private fun isSecretExist(selectionArgs: String): Boolean {
        var result = false
        var cursor: Cursor? = null
        try {
            cursor = db.query(tableName,
                null,
                "secretTitle = ?",
                arrayOf(selectionArgs),
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