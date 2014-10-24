package org.onebrick.android.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.onebrick.android.LoginManager;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyPastEventsFragment extends EventsListFragment {

    private static final String ARG_NUMBER_OF_EVENTS = "number_of_upcoming_events";
    private Set<Long> setOfUniqueEventsIds;

    public MyPastEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();

//        final Bundle args = getArguments();
//        if (args != null) {
//            numberOfUpcomingEvents = args.getInt(ARG_NUMBER_OF_EVENTS);
//        }
//        Log.i("numberOfUpcomingEvents: ", numberOfUpcomingEvents + "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView( inflater, container, savedInstanceState);
        LoginManager manager = LoginManager.getInstance(container.getContext());
        if (manager != null ){
            User user = LoginManager.getInstance(container.getContext()).getCurrentUser();
            if (user != null){
                setupEventsListeners();
                getUpcomingEvents(user.getUId());
                populatePastEvents(user.getUId());
            }
        }
        return v;
    }

    private void populatePastEvents(long userId) {
        client.getMyEvents(userId, true, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pbEventsList.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pbEventsList.setVisibility(ProgressBar.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                pbEventsList.setVisibility(ProgressBar.GONE);
                Log.i("arrayOfEvents size: ", arrayOfEvents.size() + "");
                adapter.clear();
                arrayOfEvents.clear();
                if (response != null) {
                    arrayOfEvents = Event.fromJSONArray(response);
                    removeUpcomingEvents();
                    adapter.addAll(arrayOfEvents);
                    adapter.notifyDataSetChanged();
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
    private void getUpcomingEvents(long userId) {

        client.getMyEvents(userId, false, new JsonHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                setOfUniqueEventsIds = new HashSet<Long>();
                if (response != null) {
                    setOfUniqueEventsIds = Event.getUniqueEventIds(response);
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

    protected void removeUpcomingEvents() {
        Log.i("remove inside: ", "here");
//        if (setOfUpcomingEvents != null && !setOfUpcomingEvents.isEmpty()
//                && arrayOfEvents != null && !arrayOfEvents.isEmpty()){
//            for (Event event : arrayOfEvents){
//                if (setOfUpcomingEvents.contains(event.getEventId())){
//                    Log.i("remove: ", event.getEventId() + "");
//                    arrayOfEvents.remove(event);
//                }
//            }
//        }
        if ((setOfUniqueEventsIds != null && !setOfUniqueEventsIds.isEmpty()) &&
                (arrayOfEvents != null && !arrayOfEvents.isEmpty())){
            for (Event event : arrayOfEvents){
                if (setOfUniqueEventsIds.contains(event.getEventId())){
                    arrayOfEvents.remove(event);
                }
            }
        }
    }

}
