package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

/**
 * Created by AshwinGV on 10/25/14.
 */
public class EventSearchListAdapter extends ArrayAdapter<Event> {

    private static class ViewHolder {
        ImageView ivEventImage;
        TextView ivEventName;
        ImageView ivEventLocation;
        TextView tvEventAddress;
        ImageView ivEventTime;
        TextView tvEventTime;
    }

    public EventSearchListAdapter(Context context, ArrayList<Event> events) {
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
            convertView = inflater.inflate(R.layout.front_event_info_view, parent, false);
            viewHolder.ivEventImage = (ImageView) convertView.findViewById(R.id.ivListItemEventImage);
            viewHolder.ivEventName = (TextView) convertView.findViewById(R.id.tvListItemEventName);
            viewHolder.ivEventLocation = (ImageView) convertView.findViewById(R.id.ivListItemEventLocation);
            viewHolder.tvEventAddress = (TextView) convertView.findViewById(R.id.tvEventAddress);
            viewHolder.ivEventTime = (ImageView) convertView.findViewById(R.id.ivListItemEventTime);
            viewHolder.tvEventTime = (TextView) convertView.findViewById(R.id.tvListItemEventTime);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
