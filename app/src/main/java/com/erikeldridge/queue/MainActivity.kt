package com.erikeldridge.queue

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

/**
 * sizes:
 * - base: 1505867
 * - kotlin: 1505867
 * - proto: 1745382
 */
class Store(context: Context): SQLiteOpenHelper(context, "store.db", null, 1) {
    fun put(type: String, id: String, attr: String, value: String) {
        val values = ContentValues()
        values.put(columnType, type)
        values.put(columnId, id)
        values.put(columnAttr, attr)
        values.put(columnValue, value)
        writableDatabase.insertWithOnConflict(table, "null", values, SQLiteDatabase.CONFLICT_REPLACE)
    }
    fun get(type: String, id: String, attr: String): String? {
        val cursor = readableDatabase.rawQuery(
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
        val cursor = readableDatabase.rawQuery(
                "select $columnAttr, $columnValue from $table where type=? and id=?",
                arrayOf(type, id))
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
    companion object {
        val table = "v1"
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
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTableQuery)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val phone = findViewById(R.id.phone) as TextView
        val store = Store(this)
        store.put("users", "1", "phone", "+789")
        val values = store.get("users", "1")
        val user = Models.User.newBuilder()
                .setId(1)
                .setPhone(values["phone"])
                .build()
        phone.text = user.toString()
    }
}
