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
import org.onebrick.android.models.User;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyUpcomingEventsFragment extends EventsListFragment {
    //TransferNumberOfUpcomingEvents mCallback;

    public MyUpcomingEventsFragment() {
        // Required empty public constructor
    }

//    public interface TransferNumberOfUpcomingEvents{
//        public void transferNumber(int numberOfUpcomingEvents);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mCallback = (TransferNumberOfUpcomingEvents) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement TransferNumberOfUpcomingEvents");
//        }
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
        LoginManager manager = LoginManager.getInstance(container.getContext());
        if (manager != null ){
            User user = LoginManager.getInstance(container.getContext()).getCurrentUser();
            if (user != null){
                setupEventsListeners();
                populateUpcomingEvents(user.getUId());
            }
        }
        return v;
    }

    private void populateUpcomingEvents(long userId) {
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
                adapter.clear();
                arrayOfEvents.clear();
                if (response != null){
                    arrayOfEvents = Event.fromJSONArray(response);
//                    if (arrayOfEvents != null && !arrayOfEvents.isEmpty()) {
//                        mCallback.transferNumber(arrayOfEvents.size());
//                    }
                    //populateSetOfUpcomingEvents(arrayOfEvents);
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

//    protected void populateSetOfUpcomingEvents(ArrayList<Event> arrayOfEvents) {
//        setOfUpcomingEvents = new HashSet<Long>();
//        if (!arrayOfEvents.isEmpty()){
//            for (Event event : arrayOfEvents){
//                setOfUpcomingEvents.add(event.getEventId());
//                Log.i("adding events: ", event.getEventId() + "");
//            }
//        }
//    }

}
