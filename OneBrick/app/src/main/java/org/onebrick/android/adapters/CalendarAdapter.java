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
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by AshwinGV on 10/27/14.
 */
public class CalendarAdapter extends ArrayAdapter<Event> implements StickyListHeadersAdapter {

    Context ctx;
    LayoutInflater inflater;
    ArrayList <Date> datesHeader;
    ArrayList <Event> eventList;
    int[] sectionIndices;
    private static class ViewHolder {
        TextView tvEventName;
        TextView tvEventAddress;
        TextView tvEventTime;
    }

    private static class HeaderViewHolder {
        TextView tvDate;
    }

    public CalendarAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.calendar_item, events);
        ctx = context;
        inflater = LayoutInflater.from(ctx);
        eventList = events;
        datesHeader = getDatesFromEventList(eventList);
        sectionIndices = getSectionIndicesFromEventList(eventList);
    }

    private int[] getSectionIndicesFromEventList(ArrayList<Event> eventList) {
        return null;
    }

    private ArrayList<Date> getDatesFromEventList(ArrayList<Event> eventList) {
        return null;
    }

    public void setDatesHeader(ArrayList<Date> dates) {
        datesHeader = dates;
    }

    public void setEventList(ArrayList<Event> events) {
        eventList = events;
    }

    public ArrayList<Date> getDatesHeader() {
        return datesHeader;
    }
    public ArrayList<Event> getEventList() {
        return eventList;
    }

    @Override
    public long getHeaderId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.calendar_item, parent, false);
            viewHolder.tvEventTime = (TextView) convertView.findViewById(R.id.tvCalEventTime);
            viewHolder.tvEventName = (TextView) convertView.findViewById(R.id.tvCalEventName);
            viewHolder.tvEventAddress = (TextView) convertView.findViewById(R.id.tvCalEventAddress);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if (view == null) {
            headerViewHolder = new HeaderViewHolder();
            view = inflater.inflate(R.layout.calendar_item_header, parent, false);
            headerViewHolder.tvDate = (TextView) view.findViewById(R.id.tvCalDate);
            view.setTag(headerViewHolder);
        } else {
            headerViewHolder = (HeaderViewHolder) view.getTag();
        }
        return view;
    }
}
