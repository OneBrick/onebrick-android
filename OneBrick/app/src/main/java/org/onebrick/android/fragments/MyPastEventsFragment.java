package org.onebrick.android.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import com.activeandroid.content.ContentProvider;

import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.helpers.LoginManager;
import org.onebrick.android.models.Event;

public class MyPastEventsFragment extends EventsListFragment {

    private int myChapterId;

    public MyPastEventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myChapterId = OneBrickApplication.getInstance().getChapterId();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginManager loginManager = LoginManager.getInstance(getActivity());

//        if (loginManager.getCurrentUser() != null){
//            populatePastEvents(loginManager.getCurrentUser().getUserId());
//        }
    }

    private void populatePastEvents(long userId) {
        // get all events: both past and upcoming events
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final String[] projection = null;
        String selection = Event.USER_RSVP + "=1";
        final String[] selectionArgs = null;
        final String sortOrder = null;

        // TODO use appropriate params

        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Event.class, null),
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }
}
