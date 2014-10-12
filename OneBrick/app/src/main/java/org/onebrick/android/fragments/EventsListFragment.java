package org.onebrick.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.adapters.EventsListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

public class EventsListFragment extends Fragment {
    private static final String ARG_CHAPTER_NAME = "chapter_name";
    private static final String ARG_CHAPTER_ID = "chapter_id";

    private String chapterName;
    private int chapterId;

    private EventsListAdapter adapter;

    public static EventsListFragment newInstance(String chapterName, int chapterId) {
        EventsListFragment fragment = new EventsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_NAME, chapterName);
        args.putInt(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }
    public EventsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Event> arrayOfEvents = new ArrayList<Event>();
        adapter = new EventsListAdapter(getActivity(), arrayOfEvents);

        final Bundle args = getArguments();
        if (args != null) {
            chapterName = args.getString(ARG_CHAPTER_NAME);
            chapterId = args.getInt(ARG_CHAPTER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.lvEvents);
        listView.setAdapter(adapter);

        populateJSONData(chapterId);

        return view;
    }
    private void populateJSONData(int chapterId) {
        OneBrickClient client = OneBrickApplication.getRestClient();
        client.getEventsList(chapterId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                // once success response comes back
                Log.i("INFO", "callback success"); // logcat log

                if (response != null){
                    JSONObject eventJsonObject;
                    for (int i = 0; i < response.length(); i++) {
                        Event event = null;
                        try {
                            eventJsonObject = (JSONObject) response.get(i);
                            event = new Event();
                            event.setTitle(eventJsonObject.optString("title"));
                            event.setEventStartDate(eventJsonObject.optString("field_event_date_value"));
                            event.setEventEndDate(eventJsonObject.optString("field_event_date_value2"));
                            adapter.add(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // notify adapter so new data is displayed
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }
}
