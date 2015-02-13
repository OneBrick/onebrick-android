package org.onebrick.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class ChapterTable {

    public static final String TAG = ChapterTable.class.getName();

    public static final String TABLE_NAME = "chapters";

    // TODO move to contracts class

    public interface Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String CHAPTER_ID = "chapter_id";
    }

    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + " (" + Columns._ID + " integer primary key autoincrement, " + Columns.NAME
            + " text not null, " + Columns.CHAPTER_ID + " integer not null unique);";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
