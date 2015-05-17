package org.onebrick.android.jobs;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.path.android.jobqueue.Params;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.database.ChapterTable;
import org.onebrick.android.events.FetchChaptersEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.NetworkUtil;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Chapter;
import org.onebrick.android.providers.OneBrickContentProvider;

import java.util.Map;

public class FetchChaptersJob extends OneBrickBaseJob {

    public FetchChaptersJob() {
        super(new Params(Priority.MEDIUM));
    }

    @Override
    public void onRun() throws Throwable {
        if (!NetworkUtil.isConnected(OneBrickApplication.getInstance())) {
            Utils.postEventOnUi(new FetchChaptersEvent(Status.NO_NETWORK));
            return;
        }

        OneBrickApplication app = OneBrickApplication.getInstance();

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();
        ContentResolver contentResolver = app.getContentResolver();

        Map<String, Chapter> chapters = restService.getAllChapters();
        final ContentValues values = new ContentValues(2);
        for (Map.Entry<String, Chapter> entry : chapters.entrySet()) {
            final Chapter chapter = entry.getValue();
            values.clear();
            values.put(ChapterTable.Columns.CHAPTER_ID, chapter.getChapterId());
            values.put(ChapterTable.Columns.NAME, chapter.getChapterName());
            contentResolver.insert(OneBrickContentProvider.CHAPTERS_URI, values);
        }

        Utils.postEventOnUi(new FetchChaptersEvent(Status.SUCCESS));
    }

    @Override
    protected void onCancel() {
        Utils.postEventOnUi(new FetchChaptersEvent(Status.FAILED));
    }
}
