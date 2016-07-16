package com.erikeldridge.queue

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
class Store(val context: Context): SQLiteOpenHelper(context, "store.db", null, 1) {
    fun put(type: String, id: String, attr: String, value: String) {
        writableDatabase.rawQuery(
                "insert into $table(type, id, attr, value) values(?,?,?,?)",
                arrayOf(type, id, attr, value)).close()
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
        store.put("users", "1", "phone", "+123")
        val user = Models.User.newBuilder()
                .setId(1)
                .setPhone(store.get("users", "1", "phone"))
                .build()
        phone.text = user.toString()
    }
}
