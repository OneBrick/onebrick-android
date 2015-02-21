package org.onebrick.android.models;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.onebrick.android.database.EventTable;

import java.lang.reflect.Type;

public class Event {

    private static final String TAG = "Event";

    private long _id;
    private long eventId;
    private String title;
    private String startDate;
    private String endDate;
    private String address;
    private String esnTitle;
    private String summary;
    private int rsvpCapacity;
    private int rsvpCount;
    private int userRSVP;
    private String description;
    private String coordinatorEmail;
    private String managerEmail;
    private Chapter chapter;
    private String photos;

    @Override
    public String toString() {
        return title;
    }

    public long getID() {
        return _id;
    }

    public long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getAddress() {
        return address;
    }

    public String getEsnTitle() {
        return esnTitle;
    }

    public String getSummary() {
        return summary;
    }

    public int getRsvpCapacity() {
        return rsvpCapacity;
    }

    public int getUserRSVP() {
        return userRSVP;
    }

    public int getRsvpCount() {
        return rsvpCount;
    }

    public boolean isRsvp() {
        return userRSVP == 1;
    }

    public String getDescription() {
        return description;
    }

    public String getCoordinatorEmail() {
        return coordinatorEmail;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public String getProfilePhotoUri() {
        return getProfilePhotoUri(eventId);
    }

    public void rsvp() {
        userRSVP = 1;
    }

    public void unRsvp() {
        userRSVP = 0;
    }

    // TODO: this is hack need to get image uri from server
    private static String getProfilePhotoUri(long eventId) {
        long imageId = (eventId % 20) + 1;
        return "assets://images/image" + imageId + ".jpg";
    }

    public static Event fromCursor(@NonNull Cursor cursor) {
        final Event event = new Event();
        event._id = cursor.getLong(cursor.getColumnIndexOrThrow(EventTable.Columns._ID));
        event.eventId = cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.Columns.EVENT_ID));
        int chapterId = cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.Columns.CHAPTER_ID));
        // TODO: get chapter object or make member chapterId
        event.title = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.TITLE));
        event.esnTitle = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.ESN_TITLE));
        event.address = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.ADDRESS));
        event.startDate = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.START_DATE));
        event.endDate = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.END_DATE));
        event.summary = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.SUMMARY));
        event.rsvpCapacity = cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.Columns.RSVP_CAPACITY));
        event.rsvpCount = cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.Columns.RSVP_COUNT));
        event.userRSVP = cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.Columns.USER_RSVP));
        event.description = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.DESCRIPTION));
        event.coordinatorEmail = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.COORDINATOR_EMAIL));
        event.managerEmail = cursor.getString(cursor.getColumnIndexOrThrow(EventTable.Columns.MANAGER_EMAIL));
        // TODO photos when urls are available from APIS
        return event;
    }

    public static class EventJsonDeserializer implements JsonDeserializer<Event> {
        @Override
        public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final Event event = new Event();
            event.title = jsonObject.get("title").getAsString();
            event.eventId = jsonObject.get("nid").getAsLong();
            event.esnTitle = jsonObject.get("esn_title").getAsString();
            event.startDate = jsonObject.get("field_event_date_value").getAsString();
            event.endDate = jsonObject.get("field_event_date_value2").getAsString();
            event.rsvpCapacity = jsonObject.get("field_event_max_rsvp_capacity_value").getAsInt();
            event.address = jsonObject.get("address").getAsString();

            if (jsonObject.has("manager_email")) {
                event.managerEmail = jsonObject.get("manager_email").getAsString();
            }
            // should check json return. sometimes, there are multiple coordinators
            if (jsonObject.has("coordinator_email")) {
                event.coordinatorEmail = jsonObject.get("coordinator_email").getAsString();
            }
            if (jsonObject.has("body_summary")) {
                final JsonElement jsonElement = jsonObject.get("body_summary");
                if (!jsonElement.isJsonNull()) {
                    event.summary = jsonElement.getAsString();
                }
            } else if (jsonObject.has("body_value")) {
                final JsonElement jsonElement = jsonObject.get("body_value");
                if (!jsonElement.isJsonNull()) {
                    event.summary = jsonElement.getAsString();
                }
            }
            if (jsonObject.has("rsvpCnt")) {
                event.rsvpCount = jsonObject.get("rsvpCnt").getAsInt();
            }
            if (jsonObject.has("usrRSVP")) {
                event.userRSVP = jsonObject.get("usrRSVP").getAsInt();
            }

            return event;
        }
    }
}
