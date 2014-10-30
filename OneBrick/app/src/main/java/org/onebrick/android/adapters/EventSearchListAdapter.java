package org.onebrick.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private static class ViewHolder {
        LinearLayout front;
        GridLayout back;
        RelativeLayout noEvents;
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
        Button btnRsvp;
    }

    public EventSearchListAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.item_event_list, events);
        context = context;
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

            viewHolder.btnRsvp = (Button) convertView.findViewById(R.id.btnListItemRsvp);

            viewHolder.ivFbShare = (ImageView) convertView.findViewById(R.id.ivListItemFbShare);
            viewHolder.ivTwitterShare = (ImageView) convertView.findViewById(R.id.ivListItemTwitterShare);
            viewHolder.ivShare = (ImageView) convertView.findViewById(R.id.ivListItemShare);

            viewHolder.front = (LinearLayout) convertView.findViewById(R.id.front);
            viewHolder.back = (GridLayout) convertView.findViewById(R.id.back);
            viewHolder.noEvents = (RelativeLayout) convertView.findViewById(R.id.rlNoEvents);
            viewHolder.noEvents.setVisibility(View.INVISIBLE);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Return the completed view to render on screen
        final Event event = getItem(position);
        //Toast.makeText(getContext(),"Event is "+event.toString(),Toast.LENGTH_LONG).show();
        if(event!=null && !event.getTitle().equalsIgnoreCase("Error")) {
            int eventId = (int) event.getEventId();
            int imgId = (eventId%20)+1;
            String imageUri = "assets://images/image"+imgId+".jpg";
            imgLoader.displayImage(imageUri,viewHolder.ivEventImage);
            viewHolder.tvEventName.setText("" + event.getTitle());
            viewHolder.tvEventAddress.setText("" + event.getEventAddress());
            viewHolder.tvEventDate.setText("" + Utils.getFormattedEventStartDate(
                    event.getEventStartDate()));
            viewHolder.btnRsvp.setVisibility(View.GONE);
            viewHolder.ivFbShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareInFacebook(v,event.getTitle(),event.getEventId());
                }
            });
            viewHolder.ivTwitterShare.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    shareInTwitter(v, event.getTitle(), event.getEventId());
                }
            });
            viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareInOthers(v, event.getTitle(), event.getEventId());
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

    public void shareInFacebook(View v, String title, long message) {
        String fullUrl = "https://m.facebook.com/sharer.php?u=..";
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.facebook.katana",
                    "com.facebook.katana.ShareLinkActivity");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, ONEBRICK_URL_PREFIX + message);
            v.getContext().startActivity(sharingIntent);

        } catch (Exception e) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_SUBJECT, title);
            i.putExtra(Intent.EXTRA_TEXT, ONEBRICK_URL_PREFIX + message);
            i.setData(Uri.parse(fullUrl));
            v.getContext().startActivity(i);

        }
    }

    public void shareInTwitter(View v, String title, long message) {

        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, ONEBRICK_URL_PREFIX + message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = getContext().getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolvedInfoList){
            if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name );
                resolved = true;
                break;
            }
        }
        if(resolved){
            getContext().startActivity(tweetIntent);
        }else{
            Toast.makeText(getContext(), "no twitter app ", Toast.LENGTH_SHORT).show();
            tweetIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/intent/tweet"));
            tweetIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            tweetIntent.putExtra(Intent.EXTRA_TEXT, ONEBRICK_URL_PREFIX + message);

            getContext().startActivity(tweetIntent);
        }

    }

    /**
     * social share this
     * @param view
     */
    public void shareInOthers(View view, String title, long message){
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, ONEBRICK_URL_PREFIX + message);
        view.getContext().startActivity(Intent.createChooser(intent, "share"));
    }
}
