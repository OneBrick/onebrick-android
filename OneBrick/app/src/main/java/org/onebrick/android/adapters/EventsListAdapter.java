package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.ArrayList;


public class EventsListAdapter extends ArrayAdapter<Event>{
    // View lookup cache
    private static class ViewHolder {
        TextView tvEventTitle;
        TextView tvEventStartDate;
        TextView tvEventEndDate;
        TextView tvDateDisplay;
        TextView tvEventAddress;
    }

    public EventsListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.item_event_list, events);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_event_list, parent, false);
            viewHolder.tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);
            viewHolder.tvEventStartDate = (TextView) convertView.findViewById(R.id.tvEventStartDate);
            viewHolder.tvEventEndDate = (TextView) convertView.findViewById(R.id.tvEventEndDate);
            viewHolder.tvDateDisplay = (TextView) convertView.findViewById(R.id.tvDateDisplay);
            viewHolder.tvEventAddress = (TextView) convertView.findViewById(R.id.tvEventAddress);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvEventTitle.setText(event.getTitle());
        viewHolder.tvEventStartDate.setText(Utils.getFormattedTime(event.getEventStartDate()));
        viewHolder.tvEventEndDate.setText(" - " + Utils.getFormattedTimeEndOnly(event.getEventStartDate(), event.getEventEndDate()));
        viewHolder.tvDateDisplay.setText(Utils.getFormattedTimeDateOnly(event.getEventStartDate()));
        viewHolder.tvEventAddress.setText(event.getEventAddress());
        // Return the completed view to render on screen
        return convertView;
    }

}
