package org.onebrick.android.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rush on 10/11/14.
 */
@Table(name="Events")
public class Event extends Model {

    private static final String TAG = Event.class.getName().toString();

    @Column(name="Title",
            notNull = true)
    public String title;


    @Column(name="StartDate",
            notNull = true)
    public String eventStartDate;

    @Column(name="EndDate",
            notNull = true)
    public String eventEndDate;

    @Column(name="EventId",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    public int eventId;


    @Column(name="EventAddress",
            notNull = true)
    public String eventAddress;

    @Column(name="LocationName",
            notNull = true)
    public String locationName;


    @Column(name="EventSummary")
    public String eventSummary;

    @Column(name="RsvpCapacity",
            notNull = true)
    public int maxRsvpCapacity;


    @Column(name="RsvpCount",
            notNull = true)
    public int rsvpCount;

    public String toString() {
       return ""+title;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getEventStartDate(){
        return this.eventStartDate;
    }
    public void setEventStartDate(String eventStartDate){
        this.eventStartDate = eventStartDate;
    }
    public String getEventEndDate(){
        return this.eventEndDate;
    }
    public void setEventEndDate(String eventEndDate){
        this.eventEndDate = eventEndDate;
    }

    public static Event fromJSON(JSONObject jsonObject){
        Event event = new Event();
        try{
            event.title = jsonObject.getString("title");
            event.eventId = jsonObject.getInt("nid");
            event.locationName = jsonObject.getString("esn_title");
            event.eventStartDate = jsonObject.getString("field_event_date_value");
            event.eventEndDate = jsonObject.getString("field_event_date_value2");
            event.maxRsvpCapacity = jsonObject.getInt("field_event_max_rsvp_capacity_value");
            event.eventSummary = jsonObject.getString("body_summary");
            event.eventAddress = jsonObject.getString("address");
            event.rsvpCount = jsonObject.getInt("rsvpCnt");
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        Log.i(TAG,"Saving event to database");
        event.save();
        return event;
    }
    public static ArrayList<Event> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Event> events = new ArrayList<Event>();

        for (int i=0; i<jsonArray.length() ; i++){
            JSONObject eventJson = null;
            try{
                // individual event
                eventJson = jsonArray.getJSONObject(i);
                Event event = Event.fromJSON(eventJson);
                if (event != null){
                    events.add(event);
                }
            }catch(Exception e){
                e.printStackTrace();
                continue;
            }
            // convert json to Event model

        }
        return events;
    }

}
