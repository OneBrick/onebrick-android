package org.onebrick.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.onebrick.android.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.activities.EventInfoActivity;
import org.onebrick.android.adapters.EventsListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

public class EventsListFragment extends Fragment {

    private static final String TAG = EventsListFragment.class.getName().toString();

    protected ProgressBar progressBar;
    protected ListView lvEventList;
    protected EventsListAdapter adapter;
    protected ArrayList<Event> arrayOfEvents;
    protected OneBrickClient client;
    protected ImageView shareIv;
    protected ImageView share_fb;
    protected ImageView share_tw;

    public EventsListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayOfEvents = new ArrayList<Event>();
        adapter = new EventsListAdapter(getActivity(), arrayOfEvents);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        lvEventList = (ListView) view.findViewById(R.id.lvEvents);
        lvEventList.setAdapter(adapter);

        shareIv = (ImageView) view.findViewById(R.id.ibShare);
        share_fb = (ImageView) view.findViewById(R.id.ibShareFacebook);
        share_tw = (ImageView) view.findViewById(R.id.ibShareTwitter);

        setupEventsListeners();
        return view;
    }

    private void setupEventsListeners() {

        lvEventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
                Event event = (Event) adapter.getItem(position);
                Toast.makeText(getActivity(), "The Event Title to display is :" + event.getTitle() + " with id " + event.getEventId(), Toast.LENGTH_LONG).show();
                eventInfo.putExtra("EventId",""+event.getEventId());
                startActivity(eventInfo);
            }
        });
    }

//    protected void setupShareListeners(){
//        shareIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareThis(v);
//            }
//        });
//        share_fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareThis(v);
//            }
//        });
//        share_tw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareThis(v);
//            }
//        });
//    }

//    protected void shareThis(View view){
//        Toast.makeText(getActivity(), "Social share!", Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        // Add data to the intent, the receiving app will decide what to do with it.
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Some Subject Line");
//        intent.putExtra(Intent.EXTRA_TEXT, "Body of the message!");
//        startActivity(Intent.createChooser(intent, "share"));
//    }

}
