package org.onebrick.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventListAdapter extends CursorAdapter {
    ImageLoader imgLoader;
    DateTimeFormatter dtf;

    static class ViewHolder {
        @InjectView(R.id.front)
        LinearLayout front;
        @InjectView(R.id.ivListItemEventImage)
        ImageView ivEventImage;
        @InjectView(R.id.tvListItemEventName)
        TextView tvEventName;
        @InjectView(R.id.tvListItemEventAddress)
        TextView tvEventAddress;
        @InjectView(R.id.tvListItemEventDate)
        TextView tvEventDate;
        @InjectView(R.id.btnListItemRsvp)
        Button btnRsvp;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public EventListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        imgLoader = ImageLoader.getInstance();
        dtf = DateTimeFormatter.getInstance();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        // TODO: handle no events case
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Return the completed view to render on screen
        final Event event = Event.fromCursor(cursor);
        viewHolder.front.setVisibility(View.VISIBLE);
        String[] photos = Utils.getPhotos(event);
        if (photos != null && photos.length > 0) {
            imgLoader.displayImage(photos[0], viewHolder.ivEventImage);
        }
        viewHolder.tvEventName.setText(event.getTitle());
        viewHolder.tvEventAddress.setText(event.getAddress());
        viewHolder.tvEventDate.setText(DateTimeFormatter.getInstance().getFormattedEventStartDate(
                event.getStartDate()));
        viewHolder.btnRsvp.setVisibility(View.GONE);
    }
}
