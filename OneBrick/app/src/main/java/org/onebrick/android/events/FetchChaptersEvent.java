package org.onebrick.android.events;

public class FetchChaptersEvent {
    public final Status status;

    public FetchChaptersEvent(Status status) {
        this.status = status;
    }
}
