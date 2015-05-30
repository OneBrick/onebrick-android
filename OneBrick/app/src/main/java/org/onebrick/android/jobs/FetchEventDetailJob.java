package org.onebrick.android.jobs;

import com.path.android.jobqueue.Params;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.events.FetchEventDetailEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.NetworkUtil;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

public class FetchEventDetailJob extends OneBrickBaseJob {

    private long mEventId;

    public FetchEventDetailJob(long eventId) {
        super(new Params(Priority.MEDIUM));
        mEventId = eventId;
    }

    @Override
    public void onRun() throws Throwable {
        if (!NetworkUtil.isConnected(OneBrickApplication.getInstance())) {
            Utils.postEventOnUi(new FetchEventDetailEvent(Status.NO_NETWORK));
            return;
        }

        final OneBrickService restService = OneBrickRESTClient.getInstance().getRestService();
        Event event = restService.getEventDetail(mEventId);
        event.save();

        Utils.postEventOnUi(new FetchEventDetailEvent(Status.SUCCESS));
    }

    @Override
    protected void onCancel() {
        Utils.postEventOnUi(new FetchEventDetailEvent(Status.FAILED));
    }
}
