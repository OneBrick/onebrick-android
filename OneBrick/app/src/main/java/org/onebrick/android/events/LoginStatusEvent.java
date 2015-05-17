package org.onebrick.android.events;

public class LoginStatusEvent {
    public final Status status;

    public LoginStatusEvent(Status status) {
        this.status = status;
    }
}
