package org.onebrick.android.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.onebrick.android.LoginManager;
import org.onebrick.android.OneBrickApplication;
import org.onebrick.android.activities.MyEventsActivity;
import org.onebrick.android.models.User;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyPastEventsFragment extends EventsListFragment {

    private LoginManager loginManager;

    public MyPastEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = OneBrickApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView( inflater, container, savedInstanceState);
        loginManager = LoginManager.getInstance(container.getContext());

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (loginManager != null ){
            User user = loginManager.getCurrentUser();
            if (user != null){
                setupEventsListeners();
                populatePastEvents();
            }
        }
    }

    /**
     * populate my past events
     */
    private void populatePastEvents(){
        adapter.clear();
        arrayOfEvents.clear();
        arrayOfEvents = ((MyEventsActivity)getActivity()).getPastEvents();
        if (arrayOfEvents != null && !arrayOfEvents.isEmpty()){
            adapter.addAll(arrayOfEvents);
            adapter.notifyDataSetChanged();
            pbEventsList.setVisibility(ProgressBar.GONE);
        }
    }
}
