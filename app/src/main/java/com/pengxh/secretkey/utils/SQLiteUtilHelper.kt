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
        private const val SQL_SECRET =
            "create table SecretTable(id integer primary key autoincrement,secretCategory text,secretTitle text,secretAccount text,secretPassword text,recoverable text,deleteTime text)"

        private const val SQL_NEW_SECRET =
            "create table SecretTable(id integer primary key autoincrement,secretCategory text,secretTitle text,secretAccount text,secretPassword text,secretRemarks text)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_SECRET)
        Log.d(Tag, "1.0数据库创建成功")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            db.execSQL("drop table if exists SecretTable")
            db.execSQL(SQL_NEW_SECRET)
        } else {
            db.execSQL(SQL_NEW_SECRET)
            Log.d(Tag, "2.0数据库创建成功")
        }
    }
}