package org.onebrick.android.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.onebrick.android.core.OneBrickClient;
import org.onebrick.android.R;
import org.onebrick.android.activities.EventInfoActivity;
import org.onebrick.android.adapters.EventSearchListAdapter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class EventsListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = EventsListFragment.class.getName().toString();

    @InjectView(R.id.progressBar) ProgressBar progressBar;
    @InjectView(R.id.lvEventSearchList) SwipeListView lvEvents;
    protected EventSearchListAdapter mAdapter;
    ArrayList<Event> eventList;
    protected OneBrickClient client;
    String chapterName;
    int chapterId;


    public EventsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);

        eventList = new ArrayList<>();
        mAdapter = new EventSearchListAdapter(getActivity(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
        ButterKnife.inject(this, view);
        lvEvents.setAdapter(mAdapter);
        setupEventsListeners();
        return view;
    }

    private void setupEventsListeners() {
        lvEvents.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                Intent eventInfo = new Intent(getActivity(), EventInfoActivity.class);
                final Event event = Event.fromCursor((Cursor) mAdapter.getItem(position));
                eventInfo.putExtra("EventId", "" + event.getEventId());
                startActivity(eventInfo);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
}
