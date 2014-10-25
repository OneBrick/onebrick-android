package org.onebrick.android.fragments;


import android.app.Activity;
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

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyUpcomingEventsFragment extends EventsListFragment {

    private LoginManager loginManager;

    public MyUpcomingEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView( inflater, container, savedInstanceState);
        loginManager = LoginManager.getInstance(container.getContext());

        if (loginManager.getCurrentUser() != null){
            setupEventsListeners();
            populateUpcomingEvents(loginManager.getCurrentUser().getUId());
        }
        return v;
    }

    private void populateUpcomingEvents(long userId){
        // get only upcoming events
        final long id = userId;
        client.getMyEvents(id, false, new JsonHttpResponseHandler() {

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
                arrayOfEvents.clear();
                arrayOfEvents = Event.fromJSONArray(response);
                adapter.clear();
                adapter.addAll(arrayOfEvents);
                adapter.notifyDataSetChanged();

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

}
