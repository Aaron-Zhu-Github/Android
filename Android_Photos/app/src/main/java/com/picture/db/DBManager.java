package com.picture.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Persistent database
 */
public class DBManager extends DBHelper {

    public DBManager(Context context) {
        super(context);
    }

    public Cursor rawQuery(String sqlCmd, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlCmd, selectionArgs);
        return cursor;
    }

    public Cursor query(String table, String selection, String[] selectionArgs) {
        return query(table, null, selection, selectionArgs);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
        return query(table, columns, selection, selectionArgs, null);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                        String orderBy) {
        return query(table, columns, selection, selectionArgs, orderBy, null);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                        String orderBy, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy, limit);
        return cursor;
    }

    public long insert(String table, ContentValues cvs) {
        SQLiteDatabase db = getWritableDatabase();
        long ret = db.insert(table, null, cvs);
        return ret;
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int ret = db.delete(table, whereClause, whereArgs);
        return ret;
    }

    public void deleteTable(String table) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = " delete from " + table + ";";
        db.execSQL(sql);
    }

    public int update(String tableName, ContentValues cvs, String whereCause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int ret = db.update(tableName, cvs, whereCause, whereArgs);
        return ret;
    }

    protected void closeDB(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }
}
