package org.onebrick.android.jobs;

import android.support.annotation.NonNull;

import com.activeandroid.query.Select;
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
    private String mSearchQuery;

    public FetchEventsJob(int chapterId, String searchQuery) {
        super(new Params(Priority.MEDIUM));
        mChapterId = chapterId;
        mSearchQuery = searchQuery;
    }

    @Override
    public void onRun() throws Throwable {
        if (!NetworkUtil.isConnected(OneBrickApplication.getInstance())) {
            Utils.postEventOnUi(new FetchEventsEvent(Status.NO_NETWORK));
            return;
        }

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();
        List<Event> eventList = restService.getAllEvents(mChapterId, OneBrickRESTClient.PHOTO_NUM_IN_LIST, mSearchQuery);
        deleteOldEvents();
        saveEvents(eventList, mChapterId);

        Utils.postEventOnUi(new FetchEventsEvent(Status.SUCCESS));
    }

    private void deleteOldEvents() {
        new Select().from(Event.class).where(Event.USER_RSVP + "!=1").executeSingle();
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
