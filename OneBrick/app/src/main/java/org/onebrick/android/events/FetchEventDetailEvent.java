package org.onebrick.android.events;

public class FetchEventDetailEvent {
    public final Status status;

    public FetchEventDetailEvent(Status status) {
        this.status = status;
    }
}
