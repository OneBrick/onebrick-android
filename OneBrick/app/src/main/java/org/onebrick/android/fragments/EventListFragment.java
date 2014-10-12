package org.onebrick.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.onebrick.android.R;

/**
 * Created by AshwinGV on 10/12/14.
 */
public class EventListFragment  extends Fragment {
    String chapterName;
    int chapterId;
    TextView tvFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        chapterName = args.getString("chapterName");
        chapterId = args.getInt("chapterId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_event_list,container, false);
        tvFragment = (TextView) v.findViewById(R.id.tvFragment);
        tvFragment.setText("Should display events in "+chapterName+" with id "+chapterId);
        return v;
    }
}
