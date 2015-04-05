package org.onebrick.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OneBrickDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = OneBrickDatabaseHelper.class.getName();

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "main.db";

    public OneBrickDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ChapterTable.onCreate(db);
        EventTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        ChapterTable.onUpgrade(db, oldVersion, newVersion);
        EventTable.onUpgrade(db, oldVersion, newVersion);
    }
}