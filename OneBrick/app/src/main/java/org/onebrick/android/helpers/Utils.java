package org.onebrick.android.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    private static final String TAG = "Utils";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat eventDate = new SimpleDateFormat("EEE, MMM d");
    private static SimpleDateFormat eventTime = new SimpleDateFormat("h:mm a");

    public static String getFormattedEventStartDate(String onebrickDate) {
        try {
            final Date d = getLocalTime(dateFormat.parse(onebrickDate));
            final String date = eventDate.format(d);
            final String time = eventTime.format(d);
            return date + " @ " + time;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }
    public static String getFormattedEventDate(String onebrickDate) {
        try {
            final Date d = getLocalTime(dateFormat.parse(onebrickDate));
            final String date = eventDate.format(d);
            final String time = eventTime.format(d);
            return date + " " + time;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }

    public static String getFormattedEventDateOnly(String onebrickDate) {
        try {
            final Date d = getLocalTime(dateFormat.parse(onebrickDate));
            final String date = eventDate.format(d);
            return date;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }
    public static String getFormattedEventEndTime(String onebrickDate) {
        try {
            final Date d = getLocalTime(dateFormat.parse(onebrickDate));
            final String time = eventTime.format(d);
            return time;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }

    public static Date getLocalTime(Date utc) {
        return new Date(utc.getTime() + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
    }

    public static boolean isPastEvent(String endDate){
        try {
            Date eventDate = getLocalTime(dateFormat.parse(endDate));
            Date currentDate = new Date();
            if (eventDate.before(currentDate)){
                return true;
            }
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + endDate);
        }
        return false;

    }

    public static String getFormattedTimeEndOnly(String start, String end){
        String startDate = getFormattedEventDateOnly(start);
        String endDate = getFormattedEventDateOnly(end);

        if (startDate.equals(endDate)){
            return getFormattedEventEndTime(end);
        }else{
            return getFormattedEventDate(end);
        }
    }

    @Nullable
    public static Date getDate(String onebrickDate) {
        try {
            return dateFormat.parse(onebrickDate);
        } catch (Exception e) {
            Log.w(TAG, "Exception while date format: " + onebrickDate);
            return null;
        }
    }

    /**
     * remove img tags from html inside event description
     * Please, don't expect this regular expression always works.
     *
     * @param input
     * @return
     */
    public static String removeImgTagsFromHTML(@NonNull String input){
        if (!input.isEmpty()){
            input = input.replaceAll("(<img\\b[^>]*\\bsrc\\s*=\\s*)([\"\'])((?:(?!\\2)[^>])*)\\2(\\s*[^>]*>)", "");
        }
        return input;
    }

    public static String removeHTagsFromHTML(@NonNull String input){
        if (!input.isEmpty()){
            input = input.replaceAll("<h3>|</h3>", "");
        }
        return input;
    }

    public static boolean isValidEmail(@NonNull String email){
        if (!"null".equals(email.trim().toLowerCase())){
            return true;
        }
        return false;
    }

}
