package com.erikeldridge.treestore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class TreeStore {
    final Context context;
    public final SQLiteDatabase db;
    public final static String TABLE = "treestore_table";
    public final static String COLUMN_PATH = "path";
    public final static String COLUMN_VALUE = "value";
    public final static String COLUMN_UPDATED = "updated";

    public TreeStore(Context context, SQLiteDatabase db) {
        this.context = context;
        this.db = db;
    }

    public static TreeStore open(Context context){
        return new TreeStore(context, new Helper(context).getWritableDatabase());
    }
    public void close(){
        db.close();
    }
    public void put(String path, String value) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PATH, path);
        contentValues.put(COLUMN_VALUE, value);
        db.insertWithOnConflict(TABLE, "null", contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public Map<String, String> get(String path) {
        return get(path, null, null);
    }
    public Map<String, String> get(String path, String order, Integer limit) {
        final String orderString = order == null ? null : COLUMN_PATH+" "+order;
        final String limitString = limit == null ? null : limit.toString();
        final Cursor cursor = db.query(TABLE, new String[] {COLUMN_PATH, COLUMN_VALUE},
                context.getString(R.string.sql_select_condition, COLUMN_PATH),
                new String[]{path}, null, null, orderString, limitString);
        final Map<String, String> values = toMap(cursor);
        cursor.close();
        return values;
    }
    public int delete(String path){
        return db.delete(TABLE, context.getString(R.string.sql_delete_condition, COLUMN_PATH),
                new String[]{path});
    }
    public static Map<String, String> toMap(Cursor cursor) {
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
            super(context, "treestore.db", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(context.getString(R.string.sql_create_table,
                    TABLE, COLUMN_PATH, COLUMN_VALUE, COLUMN_UPDATED));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
