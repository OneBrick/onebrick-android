package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import org.onebrick.android.R;
import org.onebrick.android.adapters.ImagePagerAdapter;
import org.onebrick.android.models.Event;

import butterknife.InjectView;

public class PhotoGalleryCard extends EventCard {
    @InjectView(R.id.pager)
    ViewPager viewPager;

    public PhotoGalleryCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_photo_gallery);

        String[] photos = mEvent.getPhotos();
        ImagePagerAdapter adapter = new ImagePagerAdapter(mContext, photos);
        viewPager.setAdapter(adapter);

        return mView;
    }
}
