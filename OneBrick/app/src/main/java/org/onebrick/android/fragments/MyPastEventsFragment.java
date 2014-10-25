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
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.Date;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyPastEventsFragment extends EventsListFragment {

    private LoginManager loginManager;

    public MyPastEventsFragment() {
        // Required empty public constructor
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
            populatePastEvents(loginManager.getCurrentUser().getUId());
        }
        return v;
    }

    /**
     * populate my past events
     */
//    private void populatePastEvents(){
//        adapter.clear();
//        arrayOfEvents.clear();
//        arrayOfEvents = ((MyEventsActivity)getActivity()).getPastEvents();
//        if (arrayOfEvents != null && !arrayOfEvents.isEmpty()){
//            adapter.addAll(arrayOfEvents);
//            adapter.notifyDataSetChanged();
//            pbEventsList.setVisibility(ProgressBar.GONE);
//        }
//    }

    private void populatePastEvents(long userId){
        // get all events: both past and upcoming events
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
                arrayOfEvents.clear();
                if (response != null) {
                    arrayOfEvents.clear();
                    arrayOfEvents = Event.fromJSONArray(response);

                    // remove upcoming event
                    final Iterator<Event> itr = arrayOfEvents.iterator();
                    final long currentTime = System.currentTimeMillis();
                    while (itr.hasNext()) {
                        final Date date = Utils.getDate(itr.next().eventStartDate);
                        if (date != null && date.getTime() > currentTime) {
                            itr.remove();
                        }
                    }

                    adapter.clear();
                    adapter.addAll(arrayOfEvents);
                    adapter.notifyDataSetChanged();

                    // remove upcoming events because current response includes both past and upcoming ones
                    //removeUpcomingEvents();
                    Log.i("size of past events:", arrayOfEvents.size() + "");
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
