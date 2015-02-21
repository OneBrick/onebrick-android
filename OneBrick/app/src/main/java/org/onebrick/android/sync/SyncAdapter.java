package org.onebrick.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.database.ChapterTable;
import org.onebrick.android.database.EventTable;
import org.onebrick.android.models.Chapter;
import org.onebrick.android.models.Event;
import org.onebrick.android.providers.OneBrickContentProvider;

import java.util.List;
import java.util.Map;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    public static final String EXTRA_SYNC_TYPE = "sync_type";
    public static final String EXTRA_CHAPTER_ID = "chapter_id";

    public static final int SYNC_CHAPTERS = 1;
    public static final int SYNC_EVENTS = 2;

    private ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        final int syncType = extras.getInt(EXTRA_SYNC_TYPE);

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();

        if (syncType == SYNC_CHAPTERS) {
            Map<String, Chapter> chapters = restService.getAllChapters();
            final ContentValues values = new ContentValues(2);
            for (Map.Entry<String, Chapter> entry : chapters.entrySet()) {
                final Chapter chapter = entry.getValue();
                values.clear();
                values.put(ChapterTable.Columns.CHAPTER_ID, chapter.getChapterId());
                values.put(ChapterTable.Columns.NAME, chapter.getChapterName());
                mContentResolver.insert(OneBrickContentProvider.CHAPTERS_URI, values);
            }
        } else if (syncType == SYNC_EVENTS) {
            final int chapterId = extras.getInt(EXTRA_CHAPTER_ID);
            List<Event> eventList = restService.getAllEvents(chapterId);

            final ContentValues values = new ContentValues(14);
            for (Event event : eventList) {
                values.clear();
                values.put(EventTable.Columns.EVENT_ID, event.getEventId());
                values.put(EventTable.Columns.CHAPTER_ID, chapterId);
                values.put(EventTable.Columns.TITLE, event.getTitle());
                values.put(EventTable.Columns.ESN_TITLE, event.getEsnTitle());
                values.put(EventTable.Columns.ADDRESS, event.getAddress());
                values.put(EventTable.Columns.START_DATE, event.getStartDate());
                values.put(EventTable.Columns.END_DATE, event.getEndDate());
                values.put(EventTable.Columns.SUMMARY, event.getSummary());
                values.put(EventTable.Columns.RSVP_CAPACITY, event.getRsvpCapacity());
                values.put(EventTable.Columns.RSVP_COUNT, event.getRsvpCount());
                values.put(EventTable.Columns.USER_RSVP, event.getUserRSVP());
                values.put(EventTable.Columns.DESCRIPTION, event.getDescription());
                values.put(EventTable.Columns.COORDINATOR_EMAIL, event.getCoordinatorEmail());
                values.put(EventTable.Columns.MANAGER_EMAIL, event.getManagerEmail());
                mContentResolver.insert(OneBrickContentProvider.EVENTS_URI, values);
            }
        }
    }
}
