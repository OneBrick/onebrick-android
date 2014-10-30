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
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeEventsFragment extends EventsListFragment {

    private static final String ARG_CHAPTER_NAME = "chapter_name";
    private static final String ARG_CHAPTER_ID = "chapter_id";


    LoginManager loginManager;



    public static HomeEventsFragment newInstance(String chapterName, int chapterId) {
        HomeEventsFragment fragment = new HomeEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_NAME, chapterName);
        args.putInt(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    public  int getChapterId() {
        return chapterId;
    }
    public String getChapterName() {
        return chapterName;
    }

    public HomeEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();
        loginManager = LoginManager.getInstance(getActivity());
        final Bundle args = getArguments();
        if (args != null) {
            chapterName = args.getString(ARG_CHAPTER_NAME);
            chapterId = args.getInt(ARG_CHAPTER_ID);
        }
        Log.i("chapter id: ", String.valueOf(chapterId));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateHomeEventsList(chapterId);
    }

    private void populateHomeEventsList(int chapterId) {
        final int cid = chapterId;
        JsonHttpResponseHandler eventListResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
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
                Log.i("INFO", "callback success"); // logcat
                aEventList.clear();
                if (response != null){
                    aEventList.addAll(Event.fromJSONArray(response, cid));
                    if(aEventList.isEmpty()) {
                        /*
                        Handle the case where there are no events in chapter
                         */
                        aEventList.clear();
                        Event e = new Event();
                        e.setTitle("Error");
                        aEventList.add(e);
                    }
                    aEventList.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                //Toast.makeText(getActivity(),"API Called error",Toast.LENGTH_SHORT).show();
                aEventList.clear();
                Event e = new Event();
                e.setTitle("Error");
                aEventList.add(e);
                aEventList.notifyDataSetChanged();
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

        };
        if(loginManager.isLoggedIn()) {
            User usr = loginManager.getCurrentUser();
            client.getEventsList(cid, usr.getUId(),eventListResponseHandler);
        } else {
            client.getEventsList(cid, -1, eventListResponseHandler);
        }

    }


}
