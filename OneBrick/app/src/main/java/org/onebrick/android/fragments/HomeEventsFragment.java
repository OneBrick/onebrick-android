package org.onebrick.android.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.activeandroid.content.ContentProvider;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;
import org.onebrick.android.models.User;

public class HomeEventsFragment extends EventsListFragment {

    private static final String TAG = HomeEventsFragment.class.getName();

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = OneBrickApplication.getInstance().getRestClient();
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

    // TODO use Service to request REST calls
    private void populateHomeEventsList(final int chapterId) {
        final JsonHttpResponseHandler eventListResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Log.d(TAG, "callback success");
                if (response != null){
                    Event.fromJSONArray(response, chapterId);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Log.e("ERROR", responseString);
                Log.e("ERROR", throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString());
                Log.e("ERROR", throwable.toString());
            }

        };
        if(loginManager.isLoggedIn()) {
            User usr = loginManager.getCurrentUser();
            client.getEventsList(chapterId, usr.getUId(), eventListResponseHandler);
        } else {
            client.getEventsList(chapterId, -1, eventListResponseHandler);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        final String[] projection = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;
        // TODO use appropriate params
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Event.class, null),
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }
}
