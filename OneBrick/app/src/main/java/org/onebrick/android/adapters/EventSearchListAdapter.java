package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.helpers.DateTimeFormatter;
import org.onebrick.android.helpers.SocialShareEmail;
import org.onebrick.android.models.Event;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by AshwinGV on 10/25/14.
 */
public class EventSearchListAdapter extends ArrayAdapter<Event> {

    private static final String ONEBRICK_URL_PREFIX = "http://onebrick.org/event/?eventid=";
    private int lastPosition = -1;
    ImageLoader imgLoader;
    DateTimeFormatter dtf;
    Context context;
    Calendar now = Calendar.getInstance();
    static class ViewHolder {
        @InjectView(R.id.front) LinearLayout front;
        @InjectView(R.id.back) GridLayout back;
        @InjectView(R.id.rlNoEvents) RelativeLayout noEvents;
        @InjectView(R.id.ivListItemEventImage) ImageView ivEventImage;
        @InjectView(R.id.tvListItemEventName) TextView tvEventName;
        @InjectView(R.id.ivListItemEventLocation) ImageView ivEventLocation;
        @InjectView(R.id.tvListItemEventAddress) TextView tvEventAddress;
        @InjectView(R.id.ivListViewEventDate) ImageView ivEventDate;
        @InjectView(R.id.tvListItemEventDate) TextView tvEventDate;
        @InjectView(R.id.ivListItemFbShare) ImageView ivFbShare;
        @InjectView(R.id.ivListItemTwitterShare) ImageView ivTwitterShare;
        @InjectView(R.id.ivListItemShare) ImageView ivShare;
        @InjectView(R.id.btnListItemRsvp) Button btnRsvp;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }


    public EventSearchListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.item_event_search_list, events);
        context = context;
        imgLoader = ImageLoader.getInstance();
        dtf = DateTimeFormatter.getInstance();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_event_search_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            viewHolder.noEvents.setVisibility(View.INVISIBLE);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Return the completed view to render on screen
        final Event event = getItem(position);
        //Toast.makeText(getContext(),"Event is "+event.toString(),Toast.LENGTH_LONG).show();
        if(event!=null && !event.getTitle().equalsIgnoreCase("Error")) {
            viewHolder.front.setVisibility(View.VISIBLE);
            viewHolder.back.setVisibility(View.VISIBLE);
            viewHolder.noEvents.setVisibility(View.GONE);
            int eventId = (int) event.getEventId();
            int imgId = (eventId%20)+1;
            String imageUri = "assets://images/image"+imgId+".jpg";
            imgLoader.displayImage(imageUri,viewHolder.ivEventImage);
            viewHolder.tvEventName.setText("" + event.getTitle());
            viewHolder.tvEventAddress.setText("" + event.getEventAddress());
            viewHolder.tvEventDate.setText("" + DateTimeFormatter.getInstance().getFormattedEventStartDate(
                    event.getEventStartDate()));
            viewHolder.btnRsvp.setVisibility(View.GONE);
            viewHolder.ivFbShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialShareEmail.shareFacebook(v, event.getTitle(), event.eventId);
                    //shareInFacebook(v,event.getTitle(),event.getEventId());
                }
            });
            viewHolder.ivTwitterShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialShareEmail.shareTwitter(v, event.getTitle(), event.getEventId());
                    //shareInTwitter(v, event.getTitle(), event.getEventId());
                }
            });
            viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //shareInOthers(v, event.getTitle(), event.getEventId());
                    SocialShareEmail.shareOthers(v, event.getTitle(), event.getEventId());
                }
            });
        } else if (event.getTitle().equalsIgnoreCase("Error")) {
            viewHolder.front.setVisibility(View.INVISIBLE);
            viewHolder.back.setVisibility(View.INVISIBLE);
            viewHolder.noEvents.setVisibility(View.VISIBLE);
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
