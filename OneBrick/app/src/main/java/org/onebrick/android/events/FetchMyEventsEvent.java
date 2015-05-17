package org.onebrick.android.events;

public class FetchMyEventsEvent {
    public final Status status;

    public FetchMyEventsEvent(Status status) {
        this.status = status;
    }
}
