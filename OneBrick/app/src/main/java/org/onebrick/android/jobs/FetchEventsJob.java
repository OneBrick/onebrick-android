package org.onebrick.android.jobs;

import android.support.annotation.NonNull;

import com.path.android.jobqueue.Params;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.events.FetchEventsEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.NetworkUtil;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.List;

public class FetchEventsJob extends OneBrickBaseJob {

    private int mChapterId;

    public FetchEventsJob(int chapterId) {
        super(new Params(Priority.MEDIUM));
        mChapterId = chapterId;
    }

    @Override
    public void onRun() throws Throwable {
        if (!NetworkUtil.isConnected(OneBrickApplication.getInstance())) {
            Utils.postEventOnUi(new FetchEventsEvent(Status.NO_NETWORK));
            return;
        }

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();
        List<Event> eventList = restService.getAllEvents(mChapterId, OneBrickRESTClient.PHOTO_NUM_IN_LIST);
        saveEvents(eventList, mChapterId);

        Utils.postEventOnUi(new FetchEventsEvent(Status.SUCCESS));
    }

    @Override
    protected void onCancel() {
        Utils.postEventOnUi(new FetchEventsEvent(Status.FAILED));
    }

    private void saveEvents(@NonNull List<Event> eventList, int chapterId) {
        for (Event event : eventList) {
            if (chapterId > 0) {
                event.setChapterId(chapterId);
            }
            event.save();
        }
    }
}
