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


public class EventsListAdapter extends ArrayAdapter<Event>{

    // View lookup cache
    private static class ViewHolder {
        ImageView ivProfilePhoto;
        TextView tvEventTitle;
        TextView tvEventStartDate;
        TextView tvEventAddress;
        ImageView fbShare;
        ImageView twitterShare;
        ImageView otherShare;
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
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_event_list, parent, false);

            viewHolder.ivProfilePhoto = (ImageView) convertView.findViewById(R.id.ivProfilePhoto);
            viewHolder.tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);
            viewHolder.tvEventStartDate = (TextView) convertView.findViewById(R.id.tvEventDate);
            viewHolder.tvEventAddress = (TextView) convertView.findViewById(R.id.tvEventAddress);
            viewHolder.fbShare = (ImageView) convertView.findViewById(R.id.ibShareFacebook);
            viewHolder.twitterShare = (ImageView) convertView.findViewById(R.id.ibShareTwitter);
            viewHolder.otherShare = (ImageView) convertView.findViewById(R.id.ibShare);

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
