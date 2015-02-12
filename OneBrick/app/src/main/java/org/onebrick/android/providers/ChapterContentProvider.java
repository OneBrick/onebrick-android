package org.onebrick.android.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.onebrick.android.database.ChapterTable;
import org.onebrick.android.database.MainDatabasehelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChapterContentProvider extends ContentProvider {

    public static final String AUTHORITY = "org.onebrick.android.provider";
    private static final String BASE_PATH = "chapters";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // used for the UriMacher
    private static final int CHAPTERS = 10;
    private static final int CHAPTER_ID = 20;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, BASE_PATH, CHAPTERS);
        sUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CHAPTER_ID);
    }

    private MainDatabasehelper mDatabasehelper;

    @Override
    public boolean onCreate() {
        mDatabasehelper = new MainDatabasehelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColumns(projection);

        queryBuilder.setTables(ChapterTable.TABLE_NAME);
        final int uriType = sUriMatcher.match(uri);

        switch (uriType) {
            case CHAPTERS:
                break;

            case CHAPTER_ID:
                queryBuilder.appendWhere(ChapterTable.Columns._ID + "=" + uri.getLastPathSegment());
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
        final int uriType = sUriMatcher.match(uri);
        long id;
        switch (uriType) {
            case CHAPTERS:
                id = db.replace(ChapterTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabasehelper.getWritableDatabase();
        final int uriType = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (uriType) {
            case CHAPTERS:
                rowsDeleted = db.delete(ChapterTable.TABLE_NAME, selection, selectionArgs);
                break;

            case CHAPTER_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ChapterTable.TABLE_NAME,
                            ChapterTable.Columns._ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(ChapterTable.TABLE_NAME,
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
        final int uriType = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (uriType) {
            case CHAPTERS:
                rowsUpdated = db.update(ChapterTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            case CHAPTER_ID:
                final String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(ChapterTable.TABLE_NAME, values,
                            ChapterTable.Columns._ID + "=" + id, null);
                } else {
                    rowsUpdated = db.update(ChapterTable.TABLE_NAME, values,
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

    private void checkColumns(String[] projection) {
        if (projection != null) {
            final String []available = {ChapterTable.Columns._ID,
                    ChapterTable.Columns.CHAPTER_ID,
                    ChapterTable.Columns.NAME};
            final Set<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            final Set<String> availableColumns = new HashSet<>(Arrays.asList(available));

            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
