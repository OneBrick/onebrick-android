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


public class EventsListAdapter extends ArrayAdapter<Event>{
    // View lookup cache
    private static class ViewHolder {
        TextView tvEventTitle;
        TextView tvEventStartDate;
        TextView tvEventEndDate;
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
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvEventTitle.setText(event.getTitle());
        viewHolder.tvEventStartDate.setText(event.getEventStartDate());
        viewHolder.tvEventEndDate.setText(event.getEventEndDate());
        // Return the completed view to render on screen
        return convertView;
    }

}
