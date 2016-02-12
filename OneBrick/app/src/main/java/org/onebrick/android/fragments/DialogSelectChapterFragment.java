package org.onebrick.android.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.activeandroid.content.ContentProvider;

import org.onebrick.android.R;
import org.onebrick.android.models.Chapter;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DialogSelectChapterFragment extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CHAPTER_LOADER = 1;

    @Bind(R.id.lvChapters)
    ListView lvChapters;

    private SimpleCursorAdapter mAdapter;
    private SelectChapterFragment.OnSelectChapterListener mListener;
    private static View mView;


    public DialogSelectChapterFragment() {
    }

    public static DialogSelectChapterFragment newInstance(String title) {
        DialogSelectChapterFragment dialog = new DialogSelectChapterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
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
        mView = inflater.inflate(R.layout.fragment_chapter_list, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        lvChapters.setAdapter(mAdapter);
        lvChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    final Cursor cursor = (Cursor) mAdapter.getItem(position);
                    final Chapter chapter = Chapter.fromCursor(cursor);
                    mListener.onSelectChapter(chapter);
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SelectChapterFragment.OnSelectChapterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mView = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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

}
