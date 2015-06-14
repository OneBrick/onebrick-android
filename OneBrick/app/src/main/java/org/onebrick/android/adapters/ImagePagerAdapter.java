package org.onebrick.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;

import butterknife.ButterKnife;

public class ImagePagerAdapter extends PagerAdapter {

    private final Context context;
    private final String[] photos;

    public ImagePagerAdapter(Context context, @NonNull String[] photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageLoader imageLoader = ImageLoader.getInstance();
        String photoUrl = photos[position];

        View view = LayoutInflater.from(context).inflate(R.layout.item_pager_image, container,
                false);

        container.addView(view);

        ImageView imageView = ButterKnife.findById(view, R.id.photo);
        imageLoader.displayImage(photoUrl, imageView);

        TextView slideNumberTextView = ButterKnife.findById(view, R.id.slide_number);
        String slideNumber = (position + 1) + "/" + photos.length;
        slideNumberTextView.setText(slideNumber);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
