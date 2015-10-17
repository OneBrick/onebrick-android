package org.onebrick.android.jobs;

import com.path.android.jobqueue.Params;

import org.onebrick.android.activities.SelectChapterActivity;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.events.FetchChaptersEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.NetworkUtil;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Chapter;

import java.util.Map;

public class FetchChaptersJob extends OneBrickBaseJob {

    SelectChapterActivity mSelectChapterActivity;

    public FetchChaptersJob() {
        super(new Params(Priority.MEDIUM));
    }

    @Override
    public void onRun() throws Throwable {
        if (!NetworkUtil.isConnected(OneBrickApplication.getInstance())) {
            Utils.postEventOnUi(new FetchChaptersEvent(Status.NO_NETWORK));

            mSelectChapterActivity.onFetchChaptersEvent(new FetchChaptersEvent(Status.NO_NETWORK));
            return;
        }

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();

        Map<String, Chapter> chapters = restService.getAllChapters();
        for (Map.Entry<String, Chapter> entry : chapters.entrySet()) {
            final Chapter chapter = entry.getValue();
            chapter.save();
        }

        Utils.postEventOnUi(new FetchChaptersEvent(Status.SUCCESS));
    }

    @Override
    protected void onCancel() {
        Utils.postEventOnUi(new FetchChaptersEvent(Status.FAILED));
    }
}
