package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

/**
 * Created by rush on 10/11/14.
 */
public class EventsListAdapter extends ArrayAdapter<Event>{
    public EventsListAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event_list, parent, false);
        }
        // Lookup view for data population
        TextView tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);
        TextView tvEventStartDate = (TextView) convertView.findViewById(R.id.tvEventStartDate);
        TextView tvEventEndDate = (TextView) convertView.findViewById(R.id.tvEventEndDate);
        // Populate the data into the template view using the data object
        tvEventTitle.setText(event.getTitle());
        tvEventStartDate.setText(event.getEventStartDate());
        tvEventEndDate.setText(event.getEventEndDate());
        // Return the completed view to render on screen
        return convertView;
    }

}
