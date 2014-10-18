package org.onebrick.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.activities.EventInfoActivity;
import org.onebrick.android.adapters.EventsListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

public class EventsListFragment extends Fragment {

    private static final String TAG = EventsListFragment.class.getName().toString();
    protected static final String ARG_CHAPTER_NAME = "chapter_name";
    protected static final String ARG_CHAPTER_ID = "chapter_id";

    private String chapterName;
    private int chapterId;

    protected ProgressBar pbEventsList;
    protected ListView lvEventList;
    protected EventsListAdapter adapter;
    protected ArrayList<Event> arrayOfEvents;
    protected OneBrickClient client;

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
        arrayOfEvents = new ArrayList<Event>();
        adapter = new EventsListAdapter(getActivity(), arrayOfEvents);
        final Bundle args = getArguments();
        if (args != null) {
            chapterName = args.getString(ARG_CHAPTER_NAME);
            chapterId = args.getInt(ARG_CHAPTER_ID);
        }
        // TODO: Prakash this is hack need to remove
        if (chapterId == 0) {
            chapterId = 101;
        }
    }

    private void setupListeners() {
        lvEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
                Event event = (Event) adapter.getItem(position);
                //Toast.makeText(getActivity(), "The Event Title to display is :"+event.getTitle()+" with id "+event.getEventId(), Toast.LENGTH_LONG).show();
                eventInfo.putExtra("EventId",""+event.getEventId());
                startActivity(eventInfo);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        pbEventsList = (ProgressBar) view.findViewById(R.id.pbEventsList);
        lvEventList = (ListView) view.findViewById(R.id.lvEvents);
        lvEventList.setAdapter(adapter);
        setupListeners();
        return view;
    }


}
