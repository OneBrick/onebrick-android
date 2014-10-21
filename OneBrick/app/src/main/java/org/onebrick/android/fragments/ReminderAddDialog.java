package org.onebrick.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;

import org.onebrick.android.R;

/**
 * Created by AshwinGV on 10/21/14.
 */
public class ReminderAddDialog extends DialogFragment {
    private Spinner spnrTimePicker;
    private Button btnAdd;
    private Button btnCancel;

    public ReminderAddDialog() {

    }

    public static ReminderAddDialog newInstance(String title) {
        ReminderAddDialog rad = new ReminderAddDialog();
        Bundle args = new Bundle();
        args.putString("Title", title);
        rad.setArguments(args);
        return rad;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_rsvp_remainder, container);
        btnAdd = (Button) v.findViewById(R.id.btnAddRemainder);
        btnCancel = (Button) v.findViewById(R.id.btnCancelRemainderAdd);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().getWindow().setTitle(""+getArguments().get("Title"));
        return v;
    }
}
