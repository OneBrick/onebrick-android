package org.onebrick.android.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormatter {
    private static final String TAG = DateTimeFormatter.class.getSimpleName();

    private static DateTimeFormatter dtf;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat eventDate = new SimpleDateFormat("EEE, MMM d");
    private static SimpleDateFormat eventTime = new SimpleDateFormat("h:mm a");
    private static SimpleDateFormat eventYear = new SimpleDateFormat("yyyy");

    private DateTimeFormatter() {
    }

    public static DateTimeFormatter getInstance() {
        if(dtf == null) {
            dtf = new DateTimeFormatter();
        }
        return dtf;
    }

    public String getFormattedEventStartDate(String onebrickDate) {
        try {
            final Date d = dateFormat.parse(onebrickDate);
            final String date = eventDate.format(d);
            final String time = eventTime.format(d);
            final String year = eventYear.format(d);
            StringBuilder builder = new StringBuilder();
            builder.append(date);
            builder.append(" ");
            builder.append(year);
            builder.append(" @ ");
            builder.append(time);
            return builder.toString();
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }
    public String getFormattedEventDate(String onebrickDate) {
        try {
            final Date d = dateFormat.parse(onebrickDate);
            final String date = eventDate.format(d);
            final String time = eventTime.format(d);
            return date + " " + time;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }

    @NonNull
    public String getFormattedEventDateOnly(String onebrickDate) {
        try {
            final Date d = dateFormat.parse(onebrickDate);
            return eventDate.format(d);
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }
    public String getFormattedEventEndTime(String onebrickDate) {
        try {
            final Date d = dateFormat.parse(onebrickDate);
            final String time = eventTime.format(d);
            return time;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }

    public String getFormattedTimeEndOnly(String start, String end){
        String startDate = getFormattedEventDateOnly(start);
        String endDate = getFormattedEventDateOnly(end);

        if (startDate.equals(endDate)){
            return getFormattedEventEndTime(end);
        }else{
            return getFormattedEventDate(end);
        }
    }

    @Nullable
    public Date getDateFromString(String dateTime) {
        try {
            return dateFormat.parse(dateTime);
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date", e);
        }
        return null;
    }

    public boolean isPastEvent(@NonNull String endDate){
        try {
            Date eventDate = dateFormat.parse(endDate);
            Date currentDate = new Date();
            if (eventDate.before(currentDate)){
                return true;
            }
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + endDate);
        }
        return false;
    }

    public boolean isRSVPOpen(@NonNull String openDate){
        try {
            Date rsvpOpenDate = dateFormat.parse(openDate);
            Date currentDate = new Date();
            if (rsvpOpenDate.after(currentDate)){
                return false;
            }
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + openDate);
        }
        return true;
    }

}
