package org.onebrick.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.activities.EventInfoActivity;
import org.onebrick.android.adapters.EventSearchListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

public class EventsListFragment extends Fragment {

    private static final String TAG = EventsListFragment.class.getName().toString();

    protected ProgressBar progressBar;
    SwipeListView lvEvents;
    EventSearchListAdapter aEventList;
    ArrayList<Event> eventList;
    protected OneBrickClient client;

    public EventsListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventList = new ArrayList<Event>();
        aEventList = new EventSearchListAdapter(getActivity(), eventList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_seach, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lvEvents = (SwipeListView) view.findViewById(R.id.lvEventSearchList);
        lvEvents.setAdapter(aEventList);
        setupEventsListeners();
        return view;
    }

    private void setupEventsListeners() {
        /*
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
                Event event = aEventList.getItem(position);
                eventInfo.putExtra("EventId",""+event.getEventId());
                startActivity(eventInfo);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });*/

        lvEvents.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
                Event event = aEventList.getItem(position);
                //Toast.makeText(getActivity(), "The Event Title to display is :" + event.getTitle() + " with id " + event.getEventId(), Toast.LENGTH_LONG).show();
                eventInfo.putExtra("EventId",""+event.getEventId());
                startActivity(eventInfo);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }
}
