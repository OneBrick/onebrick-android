package org.onebrick.android.fragments;


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
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.Date;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyPastEventsFragment extends EventsListFragment {


    private int myChapterId;

    public MyPastEventsFragment() {
        // Required empty public constructor
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
        LoginManager loginManager = LoginManager.getInstance(getActivity());

        if (loginManager.getCurrentUser() != null){
            populatePastEvents(loginManager.getCurrentUser().getUId());
        }
    }

    private void populatePastEvents(long userId){
        // get all events: both past and upcoming events
        client.getMyEvents(userId, true, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }
            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.setVisibility(ProgressBar.GONE);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                eventList.clear();
                if (response != null) {
                    eventList.clear();
                    eventList = Event.fromJSONArray(response, myChapterId);

                    // remove upcoming event
                    final Iterator<Event> itr = eventList.iterator();
                    final long currentTime = System.currentTimeMillis();
                    while (itr.hasNext()) {
                        final Date date = Utils.getDate(itr.next().eventStartDate);
                        if (date != null && date.getTime() > currentTime) {
                            itr.remove();
                        }
                    }

                    aEventList.clear();
                    aEventList.addAll(eventList);
                    aEventList.notifyDataSetChanged();
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
}
