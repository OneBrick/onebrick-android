package org.onebrick.android.models;

import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@Table(name = "events", id = BaseColumns._ID)
public class Event extends Model {

    private static final String TAG = Event.class.getName().toString();

    @Column(name="title",
            notNull = true)
    public String title;


    @Column(name="start_date",
            notNull = true)
    public String eventStartDate;

    @Column(name="end_date",
            notNull = true)
    public String eventEndDate;

    @Column(name="event_id",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    public long eventId;


    @Column(name="event_address",
            notNull = true)
    public String eventAddress;

    @Column(name="location_name",
            notNull = true)
    public String locationName;


    @Column(name="event_summary")
    public String eventSummary;

    @Column(name="rsvp_capacity",
            notNull = true)
    public int maxRsvpCapacity;


    @Column(name="rsvp_count")
    public int rsvpCount;

    @Column(name="user_rsvp")
    public int usrRSVP;

    @Column(name="description")
    public String description;

    @Column(name="coordinator_email")
    public String coordinatorEmail;

    @Column(name="manager_email")
    public String managerEmail;

    @Column(name="chapter")
    public Chapter chapter;

    @Column(name="rsvp")
    public boolean rsvp;

    @Column(name="profile_photo_uri")
    public String profilePhotoUri;

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
    public String getManagerEmail(){
        return this.managerEmail;
    }
    public String getCoordinatorEmail(){
        return this.coordinatorEmail;
    }
    public String getEventDescription() {
        return this.description;
    }
    public Chapter getChapter(){
        return this.chapter;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public static Event fromJSON(JSONObject jsonObject, Chapter ch){
        Event event = new Event();
        try{
            event.title = jsonObject.optString("title");
            event.chapter = ch;
            event.rsvp = false;
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

            event.profilePhotoUri = getProfilePhotoUri(event.eventId);
            if(jsonObject.has("usrRSVP")) {
                int usrRsvp = jsonObject.getInt("usrRSVP");
                if (usrRsvp == 1) {
                    event.rsvp = true;
                } else {
                    event.rsvp = false;
                }
            }

        }catch(JSONException e){
            Log.e(TAG, "error while saving event: " + event.eventId);
            return null;
        }
        event.save();
        return event;
    }

    // TODO: this is hack need to get image uri from server
    private static String getProfilePhotoUri(long eventId){
        long imageId = (eventId % 20) + 1;
        return "assets://images/image" + imageId + ".jpg";
    }

    // Finds existing user based on remoteId or creates new user and returns
    public static Event findOrCreateFromJson(JSONObject jsonObj, Chapter ch) {
        int eventId = jsonObj.optInt("nid");
        Event existingEvent =
                new Select().from(Event.class).where("EventId = ?", eventId).executeSingle();
        if (existingEvent != null) {
            // found and return existing
            //Log.i(TAG, "Returning existing event. Not saving new events to DB");
            if(jsonObj.has("usrRSVP")) {
                int usrRsvp = jsonObj.optInt("usrRSVP");
                if (usrRsvp == 1) {
                    existingEvent.rsvp = true;
                } else {
                    existingEvent.rsvp = false;
                }
            }
            existingEvent.save();
            return existingEvent;
        } else {
            // create and return new
            Event event = fromJSON(jsonObj,ch);
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
        int usrRsvp = -1;
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
        if(jEventObject.has("usrRSVP")) {
            usrRsvp = jEventObject.optInt("usrRSVP");
        }
        if(usrRsvp == 1) {
            e.rsvp = true;
        } else {
            e.rsvp = false;
        }
        e.description = eventDesc;
        e.managerEmail = mgrEmail;
        e.coordinatorEmail = coordEmail;
        e.save();
        return e;
    }

    public static void updateEvent(Event e) {
        e.save();
    }

    public static ArrayList<Event> fromJSONArray(JSONArray jsonArray, int ChapterId) {
        ArrayList<Event> events = new ArrayList<Event>();
        Chapter ch = Chapter.getChapterFromId(ChapterId);
        if(ch == null) {
            Log.e(TAG,"ERROR Chapter cannot be null at this point");
        }
        for (int i=0; i<jsonArray.length() ; i++){
            JSONObject eventJson = null;
            try{
                // individual event
                eventJson = jsonArray.getJSONObject(i);
                Event event = Event.findOrCreateFromJson(eventJson, ch);
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
