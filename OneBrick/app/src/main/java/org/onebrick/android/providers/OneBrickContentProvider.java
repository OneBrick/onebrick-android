package org.onebrick.android.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.onebrick.android.database.ChapterTable;
import org.onebrick.android.database.EventTable;
import org.onebrick.android.database.OneBrickDatabaseHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OneBrickContentProvider extends ContentProvider {

    public static final String AUTHORITY = "org.onebrick.android.provider";

    public static final Uri CHAPTERS_URI = Uri.parse("content://" + AUTHORITY + "/chapters");
    public static final Uri EVENTS_URI = Uri.parse("content://" + AUTHORITY + "/events");

    // used for the UriMacher
    private static final int CHAPTERS = 10;
    private static final int CHAPTER_ID = 20;
    private static final int EVENTS = 30;
    private static final int EVENT_ID = 40;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "chapters", CHAPTERS);
        sUriMatcher.addURI(AUTHORITY, "chapters/#", CHAPTER_ID);
        sUriMatcher.addURI(AUTHORITY, "events", EVENTS);
        sUriMatcher.addURI(AUTHORITY, "events/#", EVENT_ID);
    }

    private OneBrickDatabaseHelper mDatabasehelper;

    @Override
    public boolean onCreate() {
        mDatabasehelper = new OneBrickDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        final int uriTypeCode = sUriMatcher.match(uri);
        final String tableName = getTableName(uriTypeCode);
        checkColumns(projection, tableName);

        queryBuilder.setTables(getTableName(uriTypeCode));

        switch (uriTypeCode) {
            case CHAPTERS:
                break;

            case CHAPTER_ID:
                queryBuilder.appendWhere(ChapterTable.Columns._ID + "=" + uri.getLastPathSegment());
                break;

            case EVENTS:
                break;

            case EVENT_ID:
                queryBuilder.appendWhere(EventTable.Columns._ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        final SQLiteDatabase db = mDatabasehelper.getWritableDatabase();
        final Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabasehelper.getWritableDatabase();
        final int uriTypeCode = sUriMatcher.match(uri);

        long id;
        final String tableName = getTableName(uriTypeCode);
        switch (uriTypeCode) {
            case CHAPTERS:
            case EVENTS:
                id = db.replace(tableName, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabasehelper.getWritableDatabase();
        final int uriTypeCode = sUriMatcher.match(uri);
        final String tableName = getTableName(uriTypeCode);

        int rowsDeleted;
        switch (uriTypeCode) {
            case CHAPTERS:
            case EVENTS:
                rowsDeleted = db.delete(tableName, selection, selectionArgs);
                break;

            case CHAPTER_ID:
            case EVENT_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(tableName,
                            ChapterTable.Columns._ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(tableName,
                            ChapterTable.Columns._ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabasehelper.getWritableDatabase();
        final int uriTypeCode = sUriMatcher.match(uri);
        final String tableName = getTableName(uriTypeCode);

        int rowsUpdated;
        switch (uriTypeCode) {
            case CHAPTERS:
            case EVENTS:
                rowsUpdated = db.update(tableName, values, selection, selectionArgs);
                break;

            case CHAPTER_ID:
            case EVENT_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(tableName, values,
                            ChapterTable.Columns._ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(tableName, values,
                            ChapterTable.Columns._ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private String getTableName(int uriType) {
        switch (uriType) {
            case CHAPTERS:
            case CHAPTER_ID:
                return ChapterTable.TABLE_NAME;

            case EVENTS:
            case EVENT_ID:
                return EventTable.TABLE_NAME;

            default:
                return "";
        }
    }

    private void checkColumns(String[] projection, String tableName) {
        if (projection != null) {
            final String[] available;

            if (ChapterTable.TABLE_NAME.equals(tableName)) {
                available = new String[] {
                        ChapterTable.Columns._ID,
                        ChapterTable.Columns.CHAPTER_ID,
                        ChapterTable.Columns.NAME};
            } else if(EventTable.TABLE_NAME.equals(tableName)) {
                available = new String[] {
                        EventTable.Columns._ID,
                        EventTable.Columns.EVENT_ID,
                        EventTable.Columns.CHAPTER_ID,
                        EventTable.Columns.TITLE,
                        EventTable.Columns.ESN_TITLE,
                        EventTable.Columns.ADDRESS,
                        EventTable.Columns.START_DATE,
                        EventTable.Columns.END_DATE,
                        EventTable.Columns.SUMMARY,
                        EventTable.Columns.RSVP_CAPACITY,
                        EventTable.Columns.RSVP_COUNT,
                        EventTable.Columns.USER_RSVP,
                        EventTable.Columns.DESCRIPTION,
                        EventTable.Columns.COORDINATOR_EMAIL,
                        EventTable.Columns.MANAGER_EMAIL,
                        EventTable.Columns.PHOTOS};
            } else {
                available = new String[0];
            }

            final Set<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            final Set<String> availableColumns = new HashSet<>(Arrays.asList(available));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(tableName + ": unknown columns in projection");
            }
        }
    }
}
