package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.models.Event;

import butterknife.Bind;

public class ShareCard extends EventCard {
    @Bind(R.id.iv_share_fb) ImageView ivEventInfoFbShare;
    @Bind(R.id.iv_share_twitter) ImageView ivEventInfoTwitterShare;
    @Bind(R.id.iv_share) ImageView ivEventInfoGenShare;

    public ShareCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_share);

        ivEventInfoFbShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareFacebook(v, mEvent.getTitle(), mEvent.getEventId());
            }
        });

        ivEventInfoTwitterShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareTwitter(v, mEvent.getTitle(), mEvent.getEventId());
            }
        });

        ivEventInfoGenShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocialShareEmail.shareOthers(v, mEvent.getTitle(), mEvent.getEventId());
            }
        });

        return mView;
    }
}
