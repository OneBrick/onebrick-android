package org.onebrick.android.helpers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

public class SocialShareEmail {

    private static final String ONEBRICK_URL_PREFIX = "http://onebrick.org/event/?eventid=";

    public static void shareFacebook(View v, String title, long message){
        String fullUrl = "https://m.facebook.com/sharer.php?u=..";
        //String fullUrl = "https://m.facebook.com/";
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.facebook.katana",
                    "com.facebook.katana.ShareLinkActivity");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
            v.getContext().startActivity(sharingIntent);

        } catch (Exception e) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_SUBJECT, title);
            i.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
            i.setData(Uri.parse(fullUrl));
            v.getContext().startActivity(i);
        }
    }
    public static void shareTwitter(View v, String title, long message){
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = v.getContext().getPackageManager();
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
            v.getContext().startActivity(tweetIntent);
        }else{
            //Toast.makeText(v.getContext(), "no twitter app ", Toast.LENGTH_SHORT).show();
            tweetIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/intent/tweet"));
            tweetIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            tweetIntent.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
            v.getContext().startActivity(tweetIntent);
        }

    }
    public static void shareOthers(View v, String title, long message){

        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide what to do with it.
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
        v.getContext().startActivity(Intent.createChooser(intent, "share"));
    }

    public static void sendEmails(View v, String title, long message, @NonNull String email){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Question to OneBrick: " + title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
        v.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
