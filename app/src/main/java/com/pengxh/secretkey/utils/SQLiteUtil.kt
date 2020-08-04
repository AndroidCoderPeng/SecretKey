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
        secretPassword: String, recoverable: String, deleteTime: String) {
        val values = ContentValues()
        values.put("secretCategory", secretCategory)
        values.put("secretTitle", secretTitle)
        values.put("secretAccount", secretAccount)
        values.put("secretPassword", secretPassword)
        values.put("recoverable", recoverable)
        values.put("deleteTime", deleteTime)
        if (!isSecretExist(secretTitle, secretAccount)) {
            Log.d(Tag, secretAccount + "保存密码")
            db.insert(tableName, null, values)
        } else {
            //更新密码
            Log.d(Tag, secretAccount + "更新密码")
            db.update(tableName, values, "secretAccount = ?", arrayOf(secretAccount))
        }
    }

    /**
     * 加载可恢复的数据
     * */
    fun loadRecoverableData(): MutableList<SecretBean> {
        val list: MutableList<SecretBean> = ArrayList()
        val cursor =
            db.query(tableName, null, "recoverable = ?", arrayOf("0"), null, null, "id DESC") //倒序
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val resultBean = SecretBean()
            resultBean.secretCategory = cursor.getString(cursor.getColumnIndex("secretCategory"))
            resultBean.secretTitle = cursor.getString(cursor.getColumnIndex("secretTitle"))
            resultBean.secretAccount = cursor.getString(cursor.getColumnIndex("secretAccount"))
            resultBean.secretPassword = cursor.getString(cursor.getColumnIndex("secretPassword"))
            resultBean.recoverable = cursor.getString(cursor.getColumnIndex("recoverable"))
            resultBean.deleteTime = cursor.getString(cursor.getColumnIndex("deleteTime"))
            list.add(resultBean)
            //下一次循环开始
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    /**
     * 加载某个分类的数据
     * */
    fun loadCategory(category: String): MutableList<SecretBean> {
        val list: MutableList<SecretBean> = ArrayList()
        val cursor = db.query(tableName,
            null,
            "secretCategory = ? and recoverable = ?",
            arrayOf(category, "1"),
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
            resultBean.deleteTime = cursor.getString(cursor.getColumnIndex("deleteTime"))
            list.add(resultBean)
            //下一次循环开始
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    /**
     * 双重判断，title和account都相同才认为是同一个账号，否则认为是同一个账号密码
     *
     * 普通删除只是更改标志位
     * */
    fun deleteSecret(title: String, account: String) {
        val values = ContentValues()
        values.put("recoverable", "0")
        db.update(tableName,
            values,
            "secretTitle = ? and secretAccount = ?",
            arrayOf(title, account))

    }

    /**
     * 真正删除数据
     * */
    fun realDeleteSecret(title: String, account: String) {
        db.delete(tableName, "secretTitle = ? and secretAccount = ?", arrayOf(title, account))
    }

    /**
     * 查询某个实体
     * */
    @Deprecated("查询某个实体")
    private fun loadSecretBean(title: String, account: String): SecretBean {
        val resultBean = SecretBean()
        val cursor = db.query(tableName,
            null,
            "secretTitle = ? and secretAccount = ?",
            arrayOf(title, account),
            null,
            null,
            "id DESC") //倒序
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            resultBean.secretCategory = cursor.getString(cursor.getColumnIndex("secretCategory"))
            resultBean.secretTitle = cursor.getString(cursor.getColumnIndex("secretTitle"))
            resultBean.secretAccount = cursor.getString(cursor.getColumnIndex("secretAccount"))
            resultBean.secretPassword = cursor.getString(cursor.getColumnIndex("secretPassword"))
            resultBean.recoverable = cursor.getString(cursor.getColumnIndex("recoverable"))
            resultBean.deleteTime = cursor.getString(cursor.getColumnIndex("deleteTime"))
            //下一次循环开始
            cursor.moveToNext()
        }
        cursor.close()
        return resultBean
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