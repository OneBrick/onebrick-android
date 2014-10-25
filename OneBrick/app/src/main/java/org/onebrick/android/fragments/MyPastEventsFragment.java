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
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyPastEventsFragment extends EventsListFragment {

    int myChapterId;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView( inflater, container, savedInstanceState);
        User user = LoginManager.getInstance(container.getContext()).getCurrentUser();
        if (user != null){
            populatePastEvents(user.getUId());
        }
        return v;
    }

    private void populatePastEvents(long userId) {
        final int chapterId = myChapterId;
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
                progressBar.setVisibility(ProgressBar.GONE);
                Log.i("INFO", "callback success"); // logcat log
                adapter.clear();
                arrayOfEvents.clear();
                if (response != null) {
                    arrayOfEvents = Event.fromJSONArray(response,chapterId);
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
