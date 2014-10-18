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
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.models.Event;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeEventsFragment extends EventsListFragment {
    private int chapterId;

    public HomeEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();
        final Bundle args = getArguments();
        if (args != null) {
            chapterId = args.getInt(ARG_CHAPTER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView( inflater, container, savedInstanceState);
        populateHomeEventsList(chapterId);
        return v;
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home_events_list, container, false);
//    }

    private void populateHomeEventsList(int chapterId) {

        client.getEventsList(chapterId, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                pbEventsList.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                pbEventsList.setVisibility(ProgressBar.GONE);
                Log.i("INFO", "callback success"); // logcat log
                adapter.clear();
                arrayOfEvents.clear();
                if (response != null){
                    arrayOfEvents = Event.fromJSONArray(response);
                    adapter.addAll(arrayOfEvents);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pbEventsList.setVisibility(ProgressBar.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                pbEventsList.setVisibility(ProgressBar.GONE);
            }

        });
    }

}
