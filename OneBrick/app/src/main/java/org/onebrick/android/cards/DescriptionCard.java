package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import butterknife.InjectView;

public class DescriptionCard extends EventCard {

    @InjectView(R.id.tv_event_description) TextView mDescriptionTextView;

    public DescriptionCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_description);

        if (mEvent.getEventDescription() != null) {
            String eventDesc = Utils.removeImgTagsFromHTML(mEvent.getEventDescription());
            eventDesc = Utils.removeHTagsFromHTML(eventDesc);
            mDescriptionTextView.setText(Html.fromHtml(eventDesc));
        }

        return mView;
    }
}
