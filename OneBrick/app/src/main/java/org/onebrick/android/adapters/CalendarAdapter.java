package org.onebrick.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.models.Event;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by AshwinGV on 10/27/14.
 */
public class CalendarAdapter extends ArrayAdapter<Event> implements StickyListHeadersAdapter {

    Context ctx;
    LayoutInflater inflater;
    ArrayList <String> datesHeader;
    ArrayList <Event> eventList;
    int[] sectionIndices;
    DateTimeFormatter dtf;
    static class ViewHolder {
        @InjectView(R.id.tvEventName) TextView tvEventName;
        @InjectView(R.id.tvEventAddress) TextView tvEventAddress;
        @InjectView(R.id.tvEventTime) TextView tvEventTime;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    static class HeaderViewHolder {
        @InjectView(R.id.tvCalDate) TextView tvDate;

        HeaderViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    public CalendarAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.calendar_item, events);
        ctx = context;
        inflater = LayoutInflater.from(ctx);
        eventList = events;
        dtf = DateTimeFormatter.getInstance();
    }

    public void setSectionIndicesFromEventList(ArrayList<Event> eventList) {
        sectionIndices = getSectionIndicesFromEventList(eventList);
    }


    public void setDatesFromEventList(ArrayList<Event> eventList) {
        datesHeader = getDatesFromEventList(eventList);
    }

    private int[] getSectionIndicesFromEventList(ArrayList<Event> eventList) {
        Event e;
        Date lastDate = null;
        ArrayList<Integer> indices = new ArrayList<Integer>();
        ArrayList<Date> dateList = new ArrayList<Date>();
        for (int i=0;i<eventList.size();i++) {
            e = eventList.get(i);
            Date d = dtf.getDateFromString(e.getEventEndDate());
            if(lastDate == null) {
                lastDate = d;
                indices.add(i);
            } else if(d.compareTo(lastDate) == 0) {
                continue;
            } else {
                lastDate = d;
                indices.add(i);
            }
        }
        int [] sectionIndices = new int[indices.size()];
        for (int i=0;i<indices.size();i++) {
            sectionIndices[i] = indices.get(i);
        }
        return  sectionIndices;
    }


    private ArrayList<String> getDatesFromEventList(ArrayList<Event> eventList) {
        Event e;
        Date lastDate = null;
        ArrayList<String> dateList = new ArrayList<String>();
        for (int i=0;i<eventList.size();i++) {
            e = eventList.get(i);
            Date d = dtf.getDateFromString(e.getEventEndDate());
            if(lastDate == null) {
                lastDate = d;
                dateList.add(dtf.getDateOnly(d));
            } else if(d.compareTo(lastDate) == 0) {
                continue;
            } else {
                lastDate = d;
                dateList.add(dtf.getDateOnly(d));
            }
        }
        return dateList;
    }


    public void setDatesHeader(ArrayList<String> dates) {
        datesHeader = dates;
    }

    public void setEventList(ArrayList<Event> events) {
        eventList = events;
    }


    public ArrayList<String> getDatesHeader() {
        return datesHeader;
    }


    public ArrayList<Event> getEventList() {
        return eventList;
    }


    @Override
    public long getHeaderId(int position) {
        long headerId = -1;
        Event e = eventList.get(position);
        for(int i=0;i<datesHeader.size();i++) {
            String d = datesHeader.get(i);
            if(d.compareTo(dtf.getDateOnly(dtf.getDateFromString(e.getEventEndDate())))==0) {
                headerId = i;
                return headerId;
            }
        }
        Log.i("CALENDAR","This should never print");
        return headerId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Event e = eventList.get(position);
        viewHolder.tvEventName.setText(e.getTitle());
        viewHolder.tvEventAddress.setText(e.getEventAddress());
        viewHolder.tvEventTime.setText(
                dtf.getTimeOnly(
                        dtf.getDateFromString(
                                e.getEventEndDate()
                        )
                )
        );
        return convertView;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup parent) {
        HeaderViewHolder headerViewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.calendar_item_header, parent, false);
            headerViewHolder = new HeaderViewHolder(view);
            view.setTag(headerViewHolder);
        } else {
            headerViewHolder = (HeaderViewHolder) view.getTag();
        }
        // Date d = datesHeader.get((int)getHeaderId(i));
        Date d = dtf.getDateFromString(eventList.get(i).getEventEndDate());
        headerViewHolder.tvDate.setText(dtf.getDateOnly(d));
        return view;
    }
}
