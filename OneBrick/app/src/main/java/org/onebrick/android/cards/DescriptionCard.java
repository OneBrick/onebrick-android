package org.onebrick.android.cards;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.activities.EventDescriptionActivity;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import butterknife.InjectView;

public class DescriptionCard extends EventCard {

    @InjectView(R.id.tv_event_description) TextView tvEventBrief;
    @InjectView(R.id.btn_more_description) Button mLearnMore;

    public DescriptionCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_description);

        if (mEvent.getEventDescription() != null) {
            String eventDesc = Utils.removeImgTagsFromHTML(mEvent.getEventDescription());
            eventDesc = Utils.removeHTagsFromHTML(eventDesc);
            tvEventBrief.setText(Html.fromHtml(eventDesc));
        }
        mLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventDetails = new Intent(mContext, EventDescriptionActivity.class);
                eventDetails.putExtra("Details", mEvent.getEventDescription());
                mContext.startActivity(eventDetails);
                //overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        return mView;
    }
}
