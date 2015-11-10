package org.onebrick.android.jobs;

import android.support.annotation.NonNull;
import android.util.Log;

import com.path.android.jobqueue.Params;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.events.FetchMyEventsEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.helpers.NetworkUtil;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.List;

public class FetchMyEventsJob extends OneBrickBaseJob {

    public FetchMyEventsJob() {
        super(new Params(Priority.MEDIUM));
    }

    @Override
    public void onRun() throws Throwable {
        if (!NetworkUtil.isConnected(OneBrickApplication.getInstance())) {
            Utils.postEventOnUi(new FetchMyEventsEvent(Status.NO_NETWORK));
            return;
        }

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();
        String ukey = LoginManager.getInstance(null).getCurrentUserKey();
        List<Event> eventList = restService.getMyEvents(ukey, OneBrickRESTClient.INCLUDE_PAST_EVENTS,  OneBrickRESTClient.PHOTO_NUM_IN_LIST);
        saveEvents(eventList);

        Utils.postEventOnUi(new FetchMyEventsEvent(Status.SUCCESS));
    }

    @Override
    protected void onCancel() {
        Utils.postEventOnUi(new FetchMyEventsEvent(Status.FAILED));
    }

    private void saveEvents(@NonNull List<Event> eventList) {
        for (Event event : eventList) {
            Event existingEvent = Event.findById(event.getEventId());
            Log.i("event before:", "chapter id: " + event.getEventId() + "--" + event.getChapterId());
            if ((existingEvent != null) && (existingEvent.getChapterId() > 0)) {
                event.setChapterId(existingEvent.getChapterId());
                Log.i("event after:", "chapter id: " + event.getEventId() + "--" + event.getChapterId());
            }
            if (DateTimeFormatter.getInstance().isPastEvent(event.getEndDate())) {

                event.setPastEvent(true);
                Log.i("event before:", "past event: " + event.getEventId());
            }
            event.save();
        }
    }
}
