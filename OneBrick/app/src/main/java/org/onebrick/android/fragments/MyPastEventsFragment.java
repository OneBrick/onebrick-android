package org.onebrick.android.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import com.activeandroid.content.ContentProvider;

import org.onebrick.android.models.Event;

public class MyPastEventsFragment extends EventsListFragment {

    public MyPastEventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final String[] projection = null;
        String selection = Event.USER_RSVP + "=1 AND " + Event.PAST_EVENT + "=1";
        final String[] selectionArgs = null;
        final String sortOrder = Event.START_DATE + " ASC";

        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Event.class, null),
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }
}
