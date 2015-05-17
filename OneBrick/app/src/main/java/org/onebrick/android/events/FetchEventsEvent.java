package org.onebrick.android.events;

public class FetchEventsEvent {
    public final Status status;

    public FetchEventsEvent(Status status) {
        this.status = status;
    }
}
