package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.ArrayList;

/**
 * Created by AshwinGV on 10/25/14.
 */
public class EventSearchListAdapter extends ArrayAdapter<Event> {
    private int lastPosition = -1;
    ImageLoader imgLoader;
    DateTimeFormatter dtf;
    private static class ViewHolder {
        LinearLayout front;
        LinearLayout back;
        ImageView ivEventImage;
        TextView tvEventName;
        ImageView ivEventLocation;
        TextView tvEventAddress;
        ImageView ivEventDate;
        TextView tvEventDate;
        ImageView ivEventTime;
        TextView tvEventTime;
        ImageView ivFbShare;
        ImageView ivTwitterShare;
        ImageView ivShare;
    }

    public EventSearchListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.item_event_list, events);
        imgLoader = ImageLoader.getInstance();
        dtf = DateTimeFormatter.getInstance();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_event_search_list, parent, false);

            viewHolder.ivEventImage = (ImageView) convertView.findViewById(R.id.ivListItemEventImage);
            viewHolder.tvEventName = (TextView) convertView.findViewById(R.id.tvListItemEventName);

            viewHolder.ivEventLocation = (ImageView) convertView.findViewById(R.id.ivListItemEventLocation);
            viewHolder.tvEventAddress = (TextView) convertView.findViewById(R.id.tvListItemEventAddress);

            viewHolder.ivEventDate = (ImageView) convertView.findViewById(R.id.ivListViewEventDate);
            viewHolder.tvEventDate = (TextView) convertView.findViewById(R.id.tvListItemEventDate);

            viewHolder.ivFbShare = (ImageView) convertView.findViewById(R.id.ivListItemFbShare);
            viewHolder.ivTwitterShare = (ImageView) convertView.findViewById(R.id.ivListItemTwitterShare);
            viewHolder.ivShare = (ImageView) convertView.findViewById(R.id.ivListItemShare);
            viewHolder.front = (LinearLayout) convertView.findViewById(R.id.front);
            viewHolder.back = (LinearLayout) convertView.findViewById(R.id.back);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Return the completed view to render on screen
        Event event = getItem(position);
        //Toast.makeText(getContext(),"Event is "+event.toString(),Toast.LENGTH_LONG).show();
        if(event!=null) {
            int eventId = (int) event.getEventId();
            int imgId = (eventId%20)+1;
            String imageUri = "assets://images/image"+imgId+".jpg";
            imgLoader.displayImage(imageUri,viewHolder.ivEventImage);
            viewHolder.tvEventName.setText("" + event.getTitle());
            viewHolder.tvEventAddress.setText("" + event.getEventAddress());
            viewHolder.tvEventDate.setText("" + Utils.getFormattedEventStartDate(event.getEventStartDate()));
        } else {
            Toast.makeText(getContext(),"Event is null",Toast.LENGTH_LONG).show();
        }
        ((SwipeListView)parent).recycle(convertView, position);
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                (position > lastPosition)
                        ? R.anim.list_item_up_from_bottom
                        : R.anim.list_item_down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;
        return convertView;
    }
}
