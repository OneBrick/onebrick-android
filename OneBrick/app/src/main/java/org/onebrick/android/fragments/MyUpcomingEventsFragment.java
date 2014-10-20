package org.onebrick.android.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyUpcomingEventsFragment extends EventsListFragment {

    int myChapterId;
    User user;
    public MyUpcomingEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();
        myChapterId = OneBrickApplication
                .getApplicationSharedPreference()
                .getInt("MyChapterId", -1);
        user = LoginManager.getInstance(getActivity()).getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView( inflater, container, savedInstanceState);
        //User user = LoginManager.getInstance(container.getContext()).getCurrentUser();
        if (user != null){
            setupEventsListeners();
            populateUpcomingEvents(user.getUId());
        }
        return v;
    }

    @Override
    public void onResume() {
        Toast.makeText(getActivity(),"Activity Resumed",Toast.LENGTH_LONG).show();
        super.onResume();
        populateUpcomingEvents(user.getUId());
    }

    private void populateUpcomingEvents(long userId) {
        final int chapterId = myChapterId;
        client.getMyEvents(userId, false, new JsonHttpResponseHandler() {
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
                Log.i("INFO", "callback success"); // logcat log
                adapter.clear();
                arrayOfEvents.clear();
                if (response != null){
                    arrayOfEvents = Event.fromJSONArray(response, chapterId);
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

}
