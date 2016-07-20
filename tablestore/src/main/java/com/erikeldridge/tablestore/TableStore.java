package com.erikeldridge.tablestore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class TableStore {
    final Context context;
    final SQLiteDatabase db;
    public final static String TABLE = "tablestore_table";
    public final static String COLUMN_PATH = "path";
    public final static String COLUMN_VALUE = "value";
    public final static String COLUMN_UPDATED = "updated";
    public final static String COLUMN_EXPIRES = "expires";
    public TableStore(Context context, SQLiteDatabase db) {
        this.context = context;
        this.db = db;
    }
    public static TableStore open(Context context){
        return new TableStore(context, new Helper(context).getWritableDatabase());
    }
    public void close(){
        db.close();
    }
    public void put(String path, String value) {
        put(path, value, null);
    }
    public Map<String, String> get(String path) {
        return get(path, COLUMN_PATH+" desc", null);
    }
    public void put(String path, String value, Long expires) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PATH, path);
        contentValues.put(COLUMN_VALUE, value);
        contentValues.put(COLUMN_EXPIRES, expires);
        db.insertWithOnConflict(TABLE, "null", contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
    }
    public Map<String, String> get(String path, String orderBy, String limit) {
        final Cursor cursor = db.query(
                TABLE, new String[] {COLUMN_PATH, COLUMN_VALUE},
                context.getString(R.string.sql_query, COLUMN_PATH, COLUMN_EXPIRES, COLUMN_UPDATED),
                new String[]{path}, null, null, orderBy, limit);
        final Map<String, String> values = toMap(cursor);
        cursor.close();
        return values;
    }
    public Cursor query(String sql, String[] args){
        return db.rawQuery(sql, args);
    }
    protected Map<String, String> toMap(Cursor cursor) {
        final Map<String, String> values = new HashMap<>();
        cursor.moveToFirst();
        while (cursor.getCount() > 0 && !cursor.isAfterLast()) {
            values.put(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
            cursor.moveToNext();
        }
        return values;
    }
    static class Helper extends SQLiteOpenHelper {
        final Context context;
        public Helper(Context context) {
            super(context, "tablestore.db", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(context.getString(R.string.sql_create_table,
                    TABLE, COLUMN_PATH, COLUMN_VALUE, COLUMN_EXPIRES, COLUMN_UPDATED));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
