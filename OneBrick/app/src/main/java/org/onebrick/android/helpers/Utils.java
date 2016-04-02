package org.onebrick.android.helpers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.onebrick.android.R;
import org.onebrick.android.core.OneBrickApplication;

public class Utils {

    public static final String PHOTO_SEPARATOR = ",";
    private static final String EVENT_CANCELLED = "Cancelled";

    /**
     * remove img tags from html inside event description
     * Please, don't expect this regular expression always works.
     *
     * @param input
     * @return
     */
    public static String removeImgTagsFromHTML(@NonNull String input) {
        if (!input.isEmpty()) {
            input = input.replaceAll("(<img\\b[^>]*\\bsrc\\s*=\\s*)([\"\'])((?:(?!\\2)[^>])*)\\2(\\s*[^>]*>)", "");
        }
        return input;
    }

    public static String removeHTagsFromHTML(@NonNull String input) {
        if (!input.isEmpty()) {
            input = input.replaceAll("<h3>|</h3>", "");
        }
        return input;
    }

    public static boolean isValidEmail(@Nullable String email) {
        return !(email == null || "null".equals(email.trim().toLowerCase()));
    }

    public static String replaceWhiteSpace(@NonNull String input) {
        if (!input.isEmpty()) {
            input = input.replaceAll("\\s+", "+");
        }
        return input;
    }

    public static boolean isDebug() {
        return (OneBrickApplication.getInstance().getApplicationInfo().flags
                & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static void postEventOnUi(final Object event) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                OneBrickApplication.getInstance().getBus().post(event);
            }
        });
    }

    public static String[] getPhotos(String photos) {
        if (!TextUtils.isEmpty(photos)) {
            return photos.split(Utils.PHOTO_SEPARATOR);
        }
        return new String[0];
    }

    public static boolean isEventCancelled(@NonNull String eventStatus) {
        if (EVENT_CANCELLED.equalsIgnoreCase(eventStatus)) {
            return true;
        }
        return false;
    }

    public static String getRSVPStatusText(Context context, @NonNull String openDate){
        if (DateTimeFormatter.getInstance().isRSVPOpen(openDate)){
            return context.getResources().getString(R.string.rsvp);
        }else{
            return context.getResources().getString(R.string.rsvp_not_open) + " " + DateTimeFormatter.getInstance().getFormattedEventDateOnly(openDate);
        }
    }
}
