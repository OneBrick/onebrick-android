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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.onebrick.android.R;
import org.onebrick.android.activities.EventDetailActivity;
import org.onebrick.android.adapters.EventListAdapter;
import org.onebrick.android.models.Event;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static butterknife.ButterKnife.findById;

public abstract class EventsListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EventsListFragment";

    @InjectView(R.id.progressBar) ProgressBar progressBar;
    protected EventListAdapter mAdapter;
    String chapterName;
    int chapterId;
    String searchQuery;

    public EventsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_list, container, false);
        ButterKnife.inject(this, view);
        ListView listView = findById(view, R.id.lvEventList);
        mAdapter = new EventListAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                final Event event = Event.fromCursor((Cursor) mAdapter.getItem(position));
                intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.getEventId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        getLoaderManager().initLoader(0, null, this);
        return view;
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
