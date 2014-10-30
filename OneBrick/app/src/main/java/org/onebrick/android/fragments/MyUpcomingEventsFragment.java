package org.onebrick.android.fragments;



import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyUpcomingEventsFragment extends EventsListFragment {

    private int myChapterId;
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
        myChapterId = OneBrickApplication
                .getApplicationSharedPreference()
                .getInt("MyChapterId", -1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginManager = LoginManager.getInstance(getActivity());

        if (loginManager.getCurrentUser() != null){
            populateUpcomingEvents(loginManager.getCurrentUser().getUId());
        }
    }

    private void populateUpcomingEvents(long userId){
        // get only upcoming events
        final long id = userId;
        client.getMyEvents(id, false, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }
            @Override
            public void onFinish() {
                super.onFinish();
                //progressBar.setVisibility(ProgressBar.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                eventList.clear();
                eventList = Event.fromJSONArray(response, myChapterId);
                aEventList.clear();
                aEventList.addAll(eventList);
                aEventList.notifyDataSetChanged();
                progressBar.setVisibility(ProgressBar.INVISIBLE);

            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("ERROR", responseString);
                Log.e("ERROR", throwable.toString());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString());
                Log.e("ERROR", throwable.toString());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

}
