package org.onebrick.android.jobs;

import android.support.annotation.NonNull;

import com.path.android.jobqueue.Params;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.events.FetchMyEventsEvent;
import org.onebrick.android.events.Status;
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
        List<Event> eventList = restService.myEvents(ukey, 1);
        saveEvents(eventList, -1);

        Utils.postEventOnUi(new FetchMyEventsEvent(Status.SUCCESS));
    }

    @Override
    protected void onCancel() {
        Utils.postEventOnUi(new FetchMyEventsEvent(Status.FAILED));
    }

    private void saveEvents(@NonNull List<Event> eventList, int chapterId) {
        for (Event event : eventList) {
            //event.setChapterId(chapterId);
            if (chapterId > 0) {
                event.setChapterId(chapterId);
            }
            event.save();
        }
    }
}
