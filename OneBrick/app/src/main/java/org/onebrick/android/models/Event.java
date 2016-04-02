package org.onebrick.android.models;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.onebrick.android.helpers.Utils;

import java.lang.reflect.Type;

@Table(name = "event", id = BaseColumns._ID)
public class Event extends Model {

    public static final String EVENT_ID = "event_id";
    public static final String USER_RSVP = "user_rsvp";
    public static final String CHAPTER_ID = "chapter_id";
    public static final String PAST_EVENT = "past_event";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_SUMMARY = "summary";
    public static final String START_DATE = "start_date";
    public static final String EVENT_STATUS = "event_status";

    @Column(name = EVENT_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long eventId;
    @Column(name = EVENT_TITLE, index = true)
    private String title;
    @Column(name = START_DATE)
    private String startDate;
    @Column(name = "end_date")
    private String endDate;
    @Column(name = "address")
    private String address;
    @Column(name = "esn_title")
    private String esnTitle;
    @Column(name = EVENT_SUMMARY)
    private String summary;
    @Column(name = "rsvp_capacity")
    private int rsvpCapacity;
    @Column(name = "rsvp_date")
    private String rsvpOpenDate;
    @Column(name = "rsvp_count")
    private int rsvpCount;
    @Column(name = USER_RSVP)
    private int userRSVP;
    @Column(name = "coordinator_email")
    private String coordinatorEmail;
    @Column(name = "manager_email")
    private String managerEmail;
    @Column(name = CHAPTER_ID)
    private int chapterId;
    @Column(name = "past_event")
    private boolean pastEvent ;
    @Column(name = "photo")
    private String photo;
    @Column(name = EVENT_STATUS)
    private String eventStatus;

    @Override
    public String toString() {
        return title;
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

    public String getRSVPOpenDate() {
        return rsvpOpenDate;
    }

    public boolean isRsvp() {
        return userRSVP == 1;
    }

    public String getCoordinatorEmail() {
        return coordinatorEmail;
    }

    public void setCoordinatorEmail(String coordinatorEmail) {
        this.coordinatorEmail = coordinatorEmail;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
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

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    @NonNull
    public String[] getPhotos() {
        return Utils.getPhotos(photo);
    }

    public void setPastEvent(boolean pastEvent) {
        this.pastEvent = pastEvent;
    }

    // TODO: this is hack need to get image uri from server
    private static String getProfilePhotoUri(long eventId) {
        long imageId = (eventId % 20) + 1;
        return "assets://images/image" + imageId + ".jpg";
    }

    /**
     * Get event status: either open or cancelled
     * @return
     */
    public String getEventStatus() {
        return eventStatus;
    }

    public static Event fromCursor(@NonNull Cursor cursor) {
        final Event event = new Event();
        event.loadFromCursor(cursor);
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
            event.rsvpOpenDate = jsonObject.get("field_rsvp_date_value").getAsString();
            // event status either open or cancelled
            event.eventStatus = jsonObject.get("field_event_status_value").getAsString();
            event.address = jsonObject.get("address").getAsString();
            if (jsonObject.has("photos")) {
                JsonArray photos=jsonObject.get("photos").getAsJsonArray();
                if (photos.size() > 0) {
                    event.photo = decodeElements(photos);
                }
            }
            if (jsonObject.has("manager_email")) {
                final JsonElement jsonElement = jsonObject.get("manager_email");
                if (!jsonElement.isJsonNull()) {
                    event.managerEmail = jsonElement.getAsString();
                }
            }
            // should check json return. sometimes, there are multiple coordinators
            if (jsonObject.has("coordinator_email")) {
                final JsonElement jsonElement = jsonObject.get("coordinator_email");
                if (!jsonElement.isJsonNull()) {
                    event.coordinatorEmail = jsonElement.getAsString();
                }
            }
            if (jsonObject.has("body_value")) {
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

        private String decodeElements(JsonArray items) {
            StringBuilder builder = new StringBuilder();
            for (JsonElement item : items){
                if (!item.isJsonNull()) {
                    builder.append(item.getAsString());
                    builder.append(Utils.PHOTO_SEPARATOR);
                }
            }
            return builder.toString();
        }
    }

    @Nullable
    public static Event findById(long eventId) {
        return new Select().from(Event.class).where(EVENT_ID + "=?",
                eventId).executeSingle();
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
