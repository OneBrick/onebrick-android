package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class EventsListAdapter extends ArrayAdapter<Event>{

    // View lookup cache
    static class ViewHolder {
        @InjectView(R.id.ivProfilePhoto) ImageView ivProfilePhoto;
        @InjectView(R.id.tvEventTitle) TextView tvEventTitle;
        @InjectView(R.id.tvEventDate) TextView tvEventStartDate;
        @InjectView(R.id.tvEventAddress) TextView tvEventAddress;
        @InjectView(R.id.ibShareFacebook) ImageView fbShare;
        @InjectView(R.id.ibShareTwitter) ImageView twitterShare;
        @InjectView(R.id.ibShare) ImageView otherShare;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    public EventsListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.item_event_list, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Event event = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_event_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        ImageLoader imageLoader =  ImageLoader.getInstance();
        imageLoader.displayImage(event.getProfilePhotoUri(), viewHolder.ivProfilePhoto);

        viewHolder.tvEventTitle.setText(event.getTitle());
        viewHolder.tvEventStartDate.setText(Utils.getFormattedEventStartDate(
                event.getEventStartDate()));
        viewHolder.tvEventAddress.setText(event.getEventAddress());
        viewHolder.fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInFacebook(v, event.getTitle(), event.getEventId());
            }
        });
        viewHolder.twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInTwitter(v, event.getTitle(), event.getEventId());
            }
        });
        viewHolder.otherShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInOthers(v, event.getTitle(), event.getEventId());
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * social share in facebook
     * @param v
     * @param title
     * @param message
     */
    public void shareInFacebook(View v, String title, long message) {
        SocialShareEmail.shareFacebook(v, title, message);
    }

    /**
     * social share in twitter
     * @param v
     * @param title
     * @param message
     */
    public void shareInTwitter(View v, String title, long message) {
        SocialShareEmail.shareTwitter(v, title, message);
    }

    /**
     * social share this
     * @param view
     */
    public void shareInOthers(View view, String title, long message){
        SocialShareEmail.shareOthers(view, title, message);
    }
}
