package org.onebrick.android.jobs;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.path.android.jobqueue.Params;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.core.OneBrickService;
import org.onebrick.android.database.EventTable;
import org.onebrick.android.events.FetchMyEventsEvent;
import org.onebrick.android.events.Status;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.helpers.NetworkUtil;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;
import org.onebrick.android.providers.OneBrickContentProvider;

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
        ContentResolver contentResolver = OneBrickApplication.getInstance().getContentResolver();
        final ContentValues values = new ContentValues(14);
        for (Event event : eventList) {
            values.clear();
            values.put(EventTable.Columns.EVENT_ID, event.getEventId());
            if (chapterId > 0) {
                values.put(EventTable.Columns.CHAPTER_ID, chapterId);
            }
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
            contentResolver.insert(OneBrickContentProvider.EVENTS_URI, values);
        }
    }
}
