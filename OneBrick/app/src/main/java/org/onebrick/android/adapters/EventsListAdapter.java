package org.onebrick.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.onebrick.android.R;
import org.onebrick.android.helpers.Utils;
import org.onebrick.android.models.Event;

import java.util.ArrayList;
import java.util.List;


public class EventsListAdapter extends ArrayAdapter<Event>{

    private static final String ONEBRICK_URL_PREFIX = "http://onebrick.org/event/?eventid=";
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
