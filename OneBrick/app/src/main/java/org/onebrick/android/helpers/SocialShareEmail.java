package org.onebrick.android.helpers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

public class SocialShareEmail {

    private static final String ONEBRICK_URL_PREFIX = "http://onebrick.org/event/?eventid=";

    public static void sendEmails(View v, String title, long message, @NonNull String email){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Question to OneBrick: " + title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, title + ":  " + ONEBRICK_URL_PREFIX + message);
        v.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
