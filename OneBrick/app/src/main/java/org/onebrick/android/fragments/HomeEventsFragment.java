package org.onebrick.android.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.activeandroid.content.ContentProvider;

import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.models.Event;

public class HomeEventsFragment extends EventsListFragment {

    private static final String TAG = "HomeEventsFragment";

    private static final String ARG_CHAPTER_NAME = "chapter_name";
    private static final String ARG_CHAPTER_ID = "chapter_id";


    public static HomeEventsFragment newInstance(String chapterName, int chapterId) {
        HomeEventsFragment fragment = new HomeEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_NAME, chapterName);
        args.putInt(ARG_CHAPTER_ID, chapterId);
        fragment.setArguments(args);
        return fragment;
    }

    public int getChapterId() {
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

        final Bundle args = getArguments();
        if (args != null) {
            chapterName = args.getString(ARG_CHAPTER_NAME);
            chapterId = args.getInt(ARG_CHAPTER_ID);
        }
        populateHomeEventsList(chapterId);
    }

    private void populateHomeEventsList(final int chapterId) {
        OneBrickRESTClient.getInstance().requestEvents(chapterId);

//        LoginManager loginManager = LoginManager.getInstance(getActivity());
//        if(loginManager.isLoggedIn()) {
//            User usr = loginManager.getCurrentUser();
//            client.getEventsList(chapterId, usr.getUserId(), eventListResponseHandler);
//        } else {
//            client.getEventsList(chapterId, -1, eventListResponseHandler);
//        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        final String[] projection = null;
        String selection = Event.CHAPTER_ID + "=?";
        final String[] selectionArgs = new String[]{Integer.toString(chapterId)};
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
