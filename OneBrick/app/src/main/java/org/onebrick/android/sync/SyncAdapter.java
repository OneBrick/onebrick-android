package org.onebrick.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.database.ChapterTable;
import org.onebrick.android.models.Chapter;
import org.onebrick.android.providers.OneBrickContentProvider;

import java.util.List;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

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

        OneBrickApplication.getInstance().getRestClient().getChapters(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "get chapters api call success");
                List<Chapter> chapters = Chapter.listFromJson(response);
//                    final List<ContentValues> valuesList = new ArrayList<>(chapters.size());
                for (Chapter chapter : chapters) {
                    final ContentValues values = new ContentValues(2);
                    values.put(ChapterTable.Columns.CHAPTER_ID, chapter.getChapterId());
                    values.put(ChapterTable.Columns.NAME, chapter.getChapterName());
//                        valuesList.add(values);
                    mContentResolver.insert(OneBrickContentProvider.CHAPTERS_URI, values);
                }
//                    getContentResolver().bulkInsert(ChapterContentProvider.CONTENT_URI,
//                            valuesList.toArray(new ContentValues[0]));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                Log.w(TAG, "get chapters api call failed");
            }
        });
    }
}
