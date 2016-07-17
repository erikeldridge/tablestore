package com.erikeldridge.tablestore

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TableStore(val helper: SQLiteOpenHelper) {
    fun put(type: String, id: String, attr: String, value: String) {
        helper.writableDatabase.insertWithOnConflict(table, "null", ContentValues().apply {
            put(columnType, type)
            put(columnId, id)
            put(columnAttr, attr)
            put(columnValue, value)
        }, SQLiteDatabase.CONFLICT_REPLACE)
    }
    fun get(type: String, id: String, attr: String): String? {
        val cursor = helper.readableDatabase.rawQuery(
                "select $columnValue from $table where type=? and id=? and attr=?",
                arrayOf(type, id, attr))
        val values = mutableListOf<String>()
        cursor.moveToFirst()
        while (cursor.count > 0 && !cursor.isAfterLast) {
            values.add(cursor.getString(cursor.getColumnIndex(columnValue)))
            cursor.moveToNext()
        }
        cursor.close()
        return values.firstOrNull()
    }
    fun get(type: String, id: String): Map<String, String> {
        val cursor = helper.readableDatabase.query(table, arrayOf(columnAttr, columnValue),
                "type=? and id=?", arrayOf(type, id), null, null, null, null)
        val values = mutableMapOf<String, String>()
        cursor.moveToFirst()
        while (cursor.count > 0 && !cursor.isAfterLast) {
            values.put(cursor.getString(cursor.getColumnIndex(columnAttr)),
                    cursor.getString(cursor.getColumnIndex(columnValue)))
            cursor.moveToNext()
        }
        cursor.close()
        return values
    }
    fun close(){
        helper.close()
    }
    companion object {
        @JvmStatic fun open(context: Context): TableStore {
            return TableStore(Helper(context))
        }
        val table = "tablestore_table"
        val columnType = "type"
        val columnId = "id"
        val columnAttr = "attr"
        val columnValue = "value"
        val columnUpdated = "updated"
        val createTableQuery = """
        create table $table (
            $columnType string,
            $columnId string,
            $columnAttr string,
            $columnValue string,
            $columnUpdated integer(4) not null default (strftime('%s','now')),
            primary key ($columnType, $columnId, $columnAttr)
        )
        """
    }
    class Helper(context: Context): SQLiteOpenHelper(context, "tablestore_v1.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(createTableQuery)
        }
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        }
    }
}