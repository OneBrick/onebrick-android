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
import org.onebrick.android.models.Event;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class HomeEventsFragment extends EventsListFragment {

    private static final String ARG_CHAPTER_NAME = "chapter_name";
    private static final String ARG_CHAPTER_ID = "chapter_id";

    private String chapterName;
    private int chapterId;

    public static HomeEventsFragment newInstance(String chapterName, int chapterId) {
        HomeEventsFragment fragment = new HomeEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_NAME, chapterName);
        args.putInt(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();
        final Bundle args = getArguments();
        if (args != null) {
            chapterName = args.getString(ARG_CHAPTER_NAME);
            chapterId = args.getInt(ARG_CHAPTER_ID);
        }
        // TODO: Prakash this is hack need to remove
        if (chapterId == 0) {
            chapterId = 101;
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
        client.getEventsList(chapterId, new JsonHttpResponseHandler() {
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
                Log.i("INFO", "callback success"); // logcat log
                adapter.clear();
                arrayOfEvents.clear();
                if (response != null){
                    arrayOfEvents = Event.fromJSONArray(response, cid);
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

//    private void setupListeners() {
//        lvEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
//                Event event = (Event) adapter.getItem(position);
//                Toast.makeText(getActivity(), "The Event Title to display is :" + event.getTitle() + " with id " + event.getEventId(), Toast.LENGTH_LONG).show();
//                eventInfo.putExtra("EventId",""+event.getEventId());
//                startActivity(eventInfo);
//            }
//        });
//    }

}
