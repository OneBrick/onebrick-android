package org.onebrick.android.cards;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import butterknife.InjectView;

public class PhotoGalleryCard extends EventCard {
    @InjectView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;

    public PhotoGalleryCard(Context context, @NonNull Event event) {
        super(context, event);
    }

    @Override
    public View initView(@NonNull ViewGroup parent) {
        initView(parent, R.layout.card_event_detail_photo_gallery);

        final ImageLoader imageLoader = ImageLoader.getInstance();
        String[] photos = Utils.getPhotos(mEvent);
        if (photos != null && photos.length > 0){
            imageLoader.displayImage(photos[0], ivProfilePhoto);
        }
//        String photo = mEvent.getPhoto();
//        imageLoader.displayImage(photo, ivProfilePhoto);
        return mView;
    }
}
