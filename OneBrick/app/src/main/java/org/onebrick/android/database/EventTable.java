package org.onebrick.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class EventTable {

    public static final String TAG = EventTable.class.getName();

    public static final String TABLE_NAME = "events";

    // TODO move to contracts class

    public interface Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String EVENT_ID = "event_id";
        public static final String CHAPTER_ID = "chapter_id";
        public static final String TITLE = "title";
        public static final String ESN_TITLE = "esn_title";
        public static final String ADDRESS = "address";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String SUMMARY = "summary";
        public static final String RSVP_CAPACITY = "rsvp_capacity";
        public static final String RSVP_COUNT = "rsvp_count";
        public static final String USER_RSVP = "user_rsvp";
        public static final String DESCRIPTION = "description";
        public static final String COORDINATOR_EMAIL = "coordinator_email";
        public static final String MANAGER_EMAIL = "manager_email";
        public static final String PHOTOS = "PHOTOS";
    }

    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + " (" +
            Columns._ID + " integer primary key autoincrement, " +
            Columns.EVENT_ID + " integer not null unique, " +
            Columns.CHAPTER_ID + " integer not null, "  +
            Columns.TITLE + " text not null, " +
            Columns.ESN_TITLE + " text, " +
            Columns.ADDRESS + " text, " +
            Columns.START_DATE + " text, " +
            Columns.END_DATE + " text, " +
            Columns.SUMMARY + " text, " +
            Columns.RSVP_CAPACITY + " integer, " +
            Columns.RSVP_COUNT + " integer, " +
            Columns.USER_RSVP + " integer, " +
            Columns.DESCRIPTION + " text, " +
            Columns.COORDINATOR_EMAIL + " text, " +
            Columns.MANAGER_EMAIL + " text, " +
            Columns.PHOTOS + " text" +
            ");";

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
