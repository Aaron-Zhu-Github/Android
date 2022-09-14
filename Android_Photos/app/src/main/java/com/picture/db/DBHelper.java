package com.picture.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.picture.util.AssetUtil;

public class DBHelper extends SQLiteOpenHelper {
    /**
     * Name database
     */
    private static final String DB_NAME = "photos.db";
    /**
     * Current context
     */
    private Context ctx;

    public DBHelper(Context context) {
        this(context, DB_NAME, null, 1);
        this.ctx = context;
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTable(SQLiteDatabase db) {
        // tag
        String tagSql = AssetUtil.readAssets(ctx, "sql/tag.sql");
        db.execSQL(tagSql);
    }
}
