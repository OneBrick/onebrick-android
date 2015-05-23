package org.onebrick.android.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.activeandroid.content.ContentProvider;

import org.onebrick.android.R;
import org.onebrick.android.models.Chapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A fragment representing a list to select chapter.
 * Activities containing this fragment MUST implement the {@link org.onebrick.android.fragments.SelectChapterFragment.OnSelectChapterListener}
 * interface.
 */
public class SelectChapterFragment extends Fragment implements AbsListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SelectChapterFragment.class.getName();

    private static final int CHAPTER_LOADER = 1;

    @InjectView(R.id.lvChapters)
    ListView lvChapters;

    private SimpleCursorAdapter mAdapter;

    private OnSelectChapterListener mListener;

    public SelectChapterFragment() {
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(Chapter.class, null),
                null,
                null,
                null,
                Chapter.NAME + " ASC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[]{Chapter.NAME},
                new int[]{android.R.id.text1},
                0);

        getLoaderManager().initLoader(CHAPTER_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_list, container, false);
        ButterKnife.inject(this, view);
        lvChapters.setAdapter(mAdapter);
        lvChapters.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectChapterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            final Cursor cursor = (Cursor) mAdapter.getItem(position);
            final Chapter chapter = Chapter.fromCursor(cursor);
            mListener.onSelectChapter(chapter);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSelectChapterListener {
        public void onSelectChapter(@NonNull Chapter chapter);
    }
}
