package com.pengxh.secretkey.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteUtilHelper internal constructor(context: Context?,
    name: String?,
    factory: CursorFactory?,
    version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val Tag = "SQLiteUtilHelper"

        private const val SQL_NEW_SECRET =
            "create table SecretNewTable(id integer primary key autoincrement,secretCategory text,secretTitle text,secretAccount text,secretPassword text,secretRemarks text)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_NEW_SECRET)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            //创建新的数据库
            Log.d(Tag, "onUpgrade: 数据库新表创建-开始")
            db.execSQL(SQL_NEW_SECRET)
            db.execSQL("drop table if exists SecretTable") //删除旧表，表名是SecretTable
            Log.d(Tag, "onUpgrade: 数据库新表创建-结束")
        }
    }
}