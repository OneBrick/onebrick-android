package org.onebrick.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.onebrick.android.core.OneBrickApplication;
import org.onebrick.android.R;
import org.onebrick.android.adapters.NavigationChapterListAdapter;
import org.onebrick.android.models.Chapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A fragment representing a list to select chapter.
 * Activities containing this fragment MUST implement the {@link OnSelectChapterFragmentListener}
 * interface.
 */
public class SelectChapterFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = SelectChapterFragment.class.getName().toString();

    @InjectView(R.id.lvChapters) ListView lvChapters;
    private NavigationChapterListAdapter chapterListAdapter;

    private OnSelectChapterFragmentListener mListener;

    public static SelectChapterFragment newInstance() {
        SelectChapterFragment fragment = new SelectChapterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectChapterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //OneBrickApplication.getRestClient().getChapters(chapterListResponseHandler);

        chapterListAdapter = new NavigationChapterListAdapter(getActivity(),
                R.layout.drawer_nav_item, new ArrayList<Chapter>());



        ArrayList<Chapter> chapterList = (ArrayList <Chapter>)Chapter.getChapterListFromDb();
        if(chapterList.isEmpty()) {
            OneBrickApplication.getRestClient().getChapters(chapterListResponseHandler);
        } else {
            chapterListAdapter.clear();
            chapterListAdapter.addAll(chapterList);
            chapterListAdapter.notifyDataSetChanged();
            Log.d(TAG, "received chapters from db: " + chapterList.size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_list, container, false);
        ButterKnife.inject(this, view);
        lvChapters.setAdapter(chapterListAdapter);
        lvChapters.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectChapterFragmentListener) activity;
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
            Chapter ch = chapterListAdapter.getItem(position);
            mListener.onSelectChapter(ch);
        }
    }

    JsonHttpResponseHandler chapterListResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            super.onStart();
            if (mListener != null) {
                mListener.onStartLoading();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mListener != null) {
                mListener.onFinishLoading();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            final List<Chapter> chapterList = Chapter.getChapterListFromJsonObject(response);
            chapterListAdapter.addAll(chapterList);
            chapterListAdapter.notifyDataSetChanged();
            Log.d(TAG, "received chapters: " + chapterList.size());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "api called failed!");
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSelectChapterFragmentListener {
        public void onSelectChapter(@NonNull Chapter chapter);
        public void onStartLoading();
        public void onFinishLoading();
    }
}
