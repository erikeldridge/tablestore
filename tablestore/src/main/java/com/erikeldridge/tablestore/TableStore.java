package com.erikeldridge.tablestore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class TableStore {
    final SQLiteOpenHelper helper;
    public final static String TABLE = "tablestore_table";
    public final static String COLUMN_TYPE = "type";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_ATTR = "attr";
    public final static String COLUMN_VALUE = "value";
    public final static String COLUMN_UPDATED = "updated";
    public final static String COLUMN_EXPIRES = "expires";
    public TableStore(SQLiteOpenHelper helper) {
        this.helper = helper;
    }
    public static TableStore open(Context context){
        return new TableStore(new Helper(context));
    }
    public void close(){
        helper.close();
    }
    public void put(String type, String id, String attr, String value, Long expires) {

        final ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_ID, id);
        values.put(COLUMN_ATTR, attr);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_EXPIRES, expires);
        helper.getWritableDatabase().insertWithOnConflict(TABLE, "null", values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }
    public Map<String, String> get(String type, String id) {
        final Cursor cursor = helper.getReadableDatabase().query(
                TABLE, new String[]{COLUMN_ATTR, COLUMN_VALUE},
                "type=? and id=? and (expires is null or expires='' or expires > updated)",
                new String[]{type, id}, null, null, null, null);
        final Map<String, String> values = toMap(cursor);
        cursor.close();
        return values;
    }
    public String get(String type, String id, String attr) {
        final Cursor cursor = helper.getReadableDatabase().query(
                TABLE, new String[]{COLUMN_ATTR, COLUMN_VALUE},
                "type=? and id=? and attr=? and (expires is null or expires='' or expires > updated)",
                new String[]{type, id, attr}, null, null, null, null);
        final Map<String, String> values = toMap(cursor);
        cursor.close();
        return values.get(attr);
    }
    public Cursor query(String sql, String[] args){
        return helper.getReadableDatabase().rawQuery(sql, args);
    }
    protected Map<String, String> toMap(Cursor cursor) {
        final Map<String, String> values = new HashMap<>();
        cursor.moveToFirst();
        while (cursor.getCount() > 0 && !cursor.isAfterLast()) {
            values.put(cursor.getString(cursor.getColumnIndex(COLUMN_ATTR)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
            cursor.moveToNext();
        }
        return values;
    }
    static class Helper extends SQLiteOpenHelper {
        final Context context;
        public Helper(Context context) {
            super(context, "tablestore_v1.db", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(context.getString(R.string.db_v1_create_table_query,
                    TABLE, COLUMN_TYPE, COLUMN_ID, COLUMN_ATTR, COLUMN_VALUE,
                    COLUMN_EXPIRES, COLUMN_UPDATED));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
