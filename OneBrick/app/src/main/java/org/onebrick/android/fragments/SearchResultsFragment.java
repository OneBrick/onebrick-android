package org.onebrick.android.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.activeandroid.content.ContentProvider;

import org.onebrick.android.core.OneBrickRESTClient;
import org.onebrick.android.models.Event;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends EventsListFragment {

    private static final String ARG_CHAPTER_NAME = "chapter_name";
    private static final String ARG_CHAPTER_ID = "chapter_id";
    private static final String ARG_SEARCH_QUERY = "search";

    public static SearchResultsFragment newInstance(String chapterName, int chapterId, String searchQuery) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAPTER_NAME, chapterName);
        args.putInt(ARG_CHAPTER_ID, chapterId);
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args != null) {
            chapterName = args.getString(ARG_CHAPTER_NAME);
            chapterId = args.getInt(ARG_CHAPTER_ID);
            searchQuery = args.getString(ARG_SEARCH_QUERY);
        }
        populateSearchResultsList(chapterId, searchQuery);
    }

    private void populateSearchResultsList(final int chapterId, final String searchQuery) {
        OneBrickRESTClient.getInstance().requestEvents(chapterId, searchQuery);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        final String[] projection = null;
        StringBuilder selection = new StringBuilder();
        selection.append(Event.CHAPTER_ID + "=? ");
        List<String> args = new ArrayList<>();
        args.add(Integer.toString(chapterId));

        if (!TextUtils.isEmpty(searchQuery)){
            // query setting
            selection.append(" and ");
            selection.append(Event.EVENT_TITLE + " like ? ");
            selection.append(" or ");
            selection.append(Event.EVENT_SUMMARY + " like ? ");
            // parameter setting
            args.add("%" + searchQuery + "%");
            args.add("%" + searchQuery + "%");
        }
        final String sortOrder = Event.START_DATE + " ASC";
        String[] selectionArgs = args.toArray(new String[args.size()]);

        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Event.class, null),
                projection,
                selection.toString(),
                selectionArgs,
                sortOrder);
    }
}
