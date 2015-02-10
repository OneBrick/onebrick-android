package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;

import org.onebrick.android.models.Event;

public abstract class EventCard extends BaseCard {
    protected Event mEvent;

    public EventCard(Context context, @NonNull Event event) {
        super(context);
        mEvent = event;
    }
}
