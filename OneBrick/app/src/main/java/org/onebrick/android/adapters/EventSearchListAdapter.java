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
import org.onebrick.android.models.Event;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventSearchListAdapter extends CursorAdapter {
    ImageLoader imgLoader;
    DateTimeFormatter dtf;

    static class ViewHolder {
        @InjectView(R.id.front) LinearLayout front;
        @InjectView(R.id.ivListItemEventImage) ImageView ivEventImage;
        @InjectView(R.id.tvListItemEventName) TextView tvEventName;
        @InjectView(R.id.tvListItemEventAddress) TextView tvEventAddress;
        @InjectView(R.id.tvListItemEventDate) TextView tvEventDate;
        @InjectView(R.id.btnListItemRsvp) Button btnRsvp;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    public EventSearchListAdapter(Context context, Cursor cursor) {
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
            int eventId = (int) event.getEventId();
            int imgId = (eventId%20)+1;
            String imageUri = "assets://images/image"+imgId+".jpg";
            imgLoader.displayImage(imageUri,viewHolder.ivEventImage);
            viewHolder.tvEventName.setText(event.getTitle());
            viewHolder.tvEventAddress.setText(event.getEventAddress());
            viewHolder.tvEventDate.setText(DateTimeFormatter.getInstance().getFormattedEventStartDate(
                    event.getEventStartDate()));
            viewHolder.btnRsvp.setVisibility(View.GONE);
    }
}
