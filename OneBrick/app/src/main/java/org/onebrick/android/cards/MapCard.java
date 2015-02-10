package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.onebrick.android.R;
import org.onebrick.android.models.Event;

public class MapCard extends EventCard {
    public MapCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_map);

        return mView;
    }
}
