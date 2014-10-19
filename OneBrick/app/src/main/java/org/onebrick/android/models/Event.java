package org.onebrick.android.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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
    public long eventId;


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


    @Column(name="RsvpCount")
    public int rsvpCount;

    @Column(name="usrRSVP")
    public int usrRSVP;

    @Column(name="Description")
    public String description;

    @Column(name="CoordinatorEmail")
    public String coordinatorEmail;

    @Column(name="ManagerEmail")
    public String managerEmail;

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
    public String getEventAddress() {
        return eventAddress;
    }
    public void setEventAddress(String eventAddress) { this.eventAddress = eventAddress; }
    public long getEventId() {
        return this.eventId;
    }

    public String getEventDescription() {
        return this.description;
    }

    public static Event fromJSON(JSONObject jsonObject){
        Event event = new Event();
        try{
            event.title = jsonObject.optString("title");
            event.eventId = jsonObject.optLong("nid");
            event.locationName = jsonObject.optString("esn_title");
            event.eventStartDate = jsonObject.optString("field_event_date_value");
            event.eventEndDate = jsonObject.getString("field_event_date_value2");
            event.maxRsvpCapacity = jsonObject.optInt("field_event_max_rsvp_capacity_value");
            if (!jsonObject.isNull("manager_email")){
                event.managerEmail = jsonObject.optString("manager_email");
            }
            // should check json return. sometimes, there are multiple coordinators
            if (!jsonObject.isNull("coordinator_email")){
                event.coordinatorEmail = jsonObject.optString("coordinator_email");
            }
            if (!jsonObject.isNull("body_summary")){
                event.eventSummary = jsonObject.optString("body_summary");
            }else if (!jsonObject.isNull("body_value")){
                event.eventSummary = jsonObject.optString("body_value");
            }
            event.eventAddress = jsonObject.optString("address");
            event.rsvpCount = jsonObject.optInt("rsvpCnt");
            if (!jsonObject.isNull("usrRSVP")){
                event.usrRSVP = jsonObject.optInt("usrRSVP");
            }
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        Log.i(TAG,"Saving event to database");
        event.save();
        return event;
    }

    // Finds existing user based on remoteId or creates new user and returns
    public static Event findOrCreateFromJson(JSONObject json) {
        Event event = Event.fromJSON(json);
        Event existingEvent =
                new Select().from(Event.class).where("EventId = ?", event.getEventId()).executeSingle();
        if (existingEvent != null) {
            // found and return existing
            return existingEvent;
        } else {
            // create and return new
            event.save();
            return event;
        }
    }

    /*
        Returns null if no event found
     */
    public static Event findEvent(int eventId   ) {
        return new Select()
                .from(Event.class)
                .where("eventId = ?", eventId)
                .executeSingle();
    }


    /*
    Returns a updated event with manager email co-ordinator email
    and event description from json object.

    Note : The event should have already been created when this method
    is getting invoked. The existing event is searched by calling the method
    findEvent(<event_id>). find event can return null in such a scenario
    IllegalArgumentException is thrown;
     */
    public static Event getUpdatedEvent(JSONObject jEventObject) {
        int eventId;
        String mgrEmail;
        String coordEmail;
        String eventDesc;
        try{
            eventId = jEventObject.optInt("nid");
            mgrEmail = jEventObject.optString("manager_email");
            coordEmail = jEventObject.optString("coordinator_email");
            eventDesc = jEventObject.optString("body_value");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        Event e = findEvent(eventId);
        if(e == null){
            throw new IllegalArgumentException("Event cannot be null for update");
        }
        e.description = eventDesc;
        e.managerEmail = mgrEmail;
        e.coordinatorEmail = coordEmail;
        e.save();
        return e;
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
