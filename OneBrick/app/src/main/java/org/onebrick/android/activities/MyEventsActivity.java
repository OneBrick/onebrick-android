package org.onebrick.android.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.onebrick.android.LoginManager;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.SupportFragmentTabListener;
import org.onebrick.android.fragments.MyPastEventsFragment;
import org.onebrick.android.fragments.MyUpcomingEventsFragment;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyEventsActivity extends ActionBarActivity {

    public static final String UPCOMING_EVENTS_TAG = "upcoming";
    public static final String PAST_EVENTS_TAG = "past_events";

    protected ArrayList<Event> arrayOfMyUpcomingEvents;
    protected ArrayList<Event> arrayOfMyPastEvents;
    protected Set<Long> setOfUniqueEventsIds;
    protected OneBrickClient client;
    protected LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        arrayOfMyPastEvents = new ArrayList<Event>();
        arrayOfMyUpcomingEvents = new ArrayList<Event>();
        client = OneBrickApplication.getRestClient();
        loginManager = LoginManager.getInstance(this);
        fetchEvents();
        setupTabs();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }

    /**
     * fetch both upcoming past events for fragments
     */
    private void fetchEvents() {
        if (loginManager != null){
            final User user = loginManager.getCurrentUser();
            if (user != null) {
                populateUpcomingEvents(user.getUId());
            }
        }
    }

    private void populateUpcomingEvents(long userId){
        // get only upcoming events
        final long id = userId;
        client.getMyEvents(id, false, new JsonHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                arrayOfMyUpcomingEvents.clear();
                setOfUniqueEventsIds = new HashSet<Long>();
                if (response != null) {
                    arrayOfMyUpcomingEvents = Event.fromJSONArray(response);
                    setOfUniqueEventsIds = Event.getUniqueEventIds(response);
                    populatePastEvents(id);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("ERROR", responseString);
                Log.e("ERROR", throwable.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString());
                Log.e("ERROR", throwable.toString());
            }
        });
    }

    private void populatePastEvents(long userId){
        // get all events: both past and upcoming events
        client.getMyEvents(userId, true, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                //pbEventsList.setVisibility(ProgressBar.VISIBLE);
            }
            @Override
            public void onFinish() {
                super.onFinish();
                //pbEventsList.setVisibility(ProgressBar.GONE);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                arrayOfMyPastEvents.clear();
                if (response != null) {
                    arrayOfMyPastEvents = Event.fromJSONArray(response);
                    // remove upcoming events because current response includes both past and upcoming ones
                    removeUpcomingEvents();
                    Log.i("size of past events:", arrayOfMyPastEvents.size() + "");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("ERROR", responseString);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString());
                Log.e("ERROR", throwable.toString());
            }
        });
    }

    public ArrayList<Event> getUpcomingEvents() {
        return arrayOfMyUpcomingEvents;
    }

    public ArrayList<Event> getPastEvents() {
        return arrayOfMyPastEvents;
    }

    private void setupTabs() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab tab1 = actionBar
                .newTab()
                .setText(R.string.upcoming)
                .setTabListener(new SupportFragmentTabListener<MyUpcomingEventsFragment>(R.id.flMyEventsContainer, this,
                        UPCOMING_EVENTS_TAG, MyUpcomingEventsFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        ActionBar.Tab tab2 = actionBar
                .newTab()
                .setText(R.string.past_events)
                .setTabListener(new SupportFragmentTabListener<MyPastEventsFragment>(R.id.flMyEventsContainer, this,
                        PAST_EVENTS_TAG, MyPastEventsFragment.class));
        actionBar.addTab(tab2);
    }

    protected void removeUpcomingEvents() {
        if ((setOfUniqueEventsIds != null && !setOfUniqueEventsIds.isEmpty()) &&
                (arrayOfMyPastEvents != null && !arrayOfMyPastEvents.isEmpty())) {
            for (Event event : arrayOfMyPastEvents) {
                if (setOfUniqueEventsIds.contains(event.getEventId())) {
                    Log.i("remove upcoming event: ", event.getEventId() + "");
                    arrayOfMyPastEvents.remove(event);
                }
            }
        }
    }
}
