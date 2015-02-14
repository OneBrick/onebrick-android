package org.onebrick.android.models;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


@Table(name = "events", id = BaseColumns._ID)
public class Event extends Model {

    private static final String TAG = "Event";

    @Column(name="event_id",
            notNull = true, unique=true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    public long eventId;

    @Column(name="title",
            notNull = true)
    public String title;

    @Column(name="start_date",
            notNull = true)
    public String startDate;

    @Column(name="end_date",
            notNull = true)
    public String endDate;

    @Column(name="address",
            notNull = true)
    public String address;

    @Column(name="esn_title",
            notNull = true)
    public String esnTitle;

    @Column(name="summary")
    public String summary;

    @Column(name="rsvp_capacity",
            notNull = true)
    public int rsvpCapacity;

    @Column(name="rsvp_count")
    public int rsvpCount;

    @Column(name="user_rsvp")
    public int userRSVP;

    @Column(name="description")
    public String description;

    @Column(name="coordinator_email")
    public String coordinatorEmail;

    @Column(name="manager_email")
    public String managerEmail;

    @Column(name="chapter")
    public Chapter chapter;

    @Column(name="photos")
    public List<String> photos;

    public String toString() {
       return ""+title;
    }

    public String getTitle(){
        return this.title;
    }
    public String getStartDate(){
        return this.startDate;
    }
    public String getEndDate(){
        return this.endDate;
    }
    public String getAddress() {
        return address;
    }
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
        return getProfilePhotoUri(eventId);
    }

    public static Event fromJSON(JSONObject jsonObject, Chapter ch){
        Event event = new Event();
        try{
            event.title = jsonObject.optString("title");
            event.chapter = ch;
            event.eventId = jsonObject.optLong("nid");
            event.esnTitle = jsonObject.optString("esn_title");
            event.startDate = jsonObject.optString("field_event_date_value");
            event.endDate = jsonObject.getString("field_event_date_value2");
            event.rsvpCapacity = jsonObject.optInt("field_event_max_rsvp_capacity_value");
            if (!jsonObject.isNull("manager_email")){
                event.managerEmail = jsonObject.optString("manager_email");
            }
            // should check json return. sometimes, there are multiple coordinators
            if (!jsonObject.isNull("coordinator_email")){
                event.coordinatorEmail = jsonObject.optString("coordinator_email");
            }
            if (!jsonObject.isNull("body_summary")){
                event.summary = jsonObject.optString("body_summary");
            }else if (!jsonObject.isNull("body_value")){
                event.summary = jsonObject.optString("body_value");
            }
            event.address = jsonObject.optString("address");
            event.rsvpCount = jsonObject.optInt("rsvpCnt");
            if (!jsonObject.isNull("usrRSVP")){
                event.userRSVP = jsonObject.optInt("usrRSVP");
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
                new Select().from(Event.class).where("event_id = ?", eventId).executeSingle();
        if (existingEvent != null) {
            existingEvent.save();
            return existingEvent;
        } else {
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
                .where("event_id = ?", eventId)
                .executeSingle();
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

    public static Event fromCursor(@NonNull Cursor cursor) {
        final Event event = new Event();
        event.loadFromCursor(cursor);
        return event;
    }
}
