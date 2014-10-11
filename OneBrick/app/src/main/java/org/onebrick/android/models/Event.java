package org.onebrick.android.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rush on 10/11/14.
 */
public class Event {
    private String title;
    private String eventStartDate;
    private String eventEndDate;

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
            event.title = jsonObject.getString("text");
            event.eventStartDate = jsonObject.getString("text");
            event.eventEndDate = jsonObject.getString("text");

        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        return event;
    }
    public static ArrayList<Event> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Event> events = new ArrayList<Event>();

        for (int i=0; i<jsonArray.length() ; i++){
            JSONObject eventJson = null;
            try{
                // individual event
                eventJson = jsonArray.getJSONObject(i);
            }catch(Exception e){
                e.printStackTrace();
                continue;
            }
            // convert json to Event model
            Event event = Event.fromJSON(eventJson);
            if (event != null){
                events.add(event);
            }
        }
        return events;
    }

}
