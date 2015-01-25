package org.onebrick.android.helpers;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeFormatter {
    private static final String TAG = "DateTimeFormatter";

    private static DateTimeFormatter dtf;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat eventDate = new SimpleDateFormat("EEE, MMM d");
    private static SimpleDateFormat eventTime = new SimpleDateFormat("h:mm a");

    private DateTimeFormatter() {};

    public static DateTimeFormatter getInstance() {
        if(dtf == null) {
            dtf = new DateTimeFormatter();
        }
        return dtf;
    }

    public String getFormattedEventStartDate(String onebrickDate) {
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
    public String getFormattedEventDate(String onebrickDate) {
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

    public String getFormattedEventDateOnly(String onebrickDate) {
        try {
            final Date d = getLocalTime(dateFormat.parse(onebrickDate));
            final String date = eventDate.format(d);
            return date;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }
    public String getFormattedEventEndTime(String onebrickDate) {
        try {
            final Date d = getLocalTime(dateFormat.parse(onebrickDate));
            final String time = eventTime.format(d);
            return time;
        } catch (ParseException e) {
            Log.e(TAG, "cannot parse date: " + onebrickDate);
        }
        return "";
    }

    public Date getLocalTime(Date utc) {
        return new Date(utc.getTime() + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
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
    public Date getDate(String onebrickDate) {
        try {
            return dateFormat.parse(onebrickDate);
        } catch (Exception e) {
            Log.w(TAG, "Exception while date format: " + onebrickDate);
            return null;
        }
    }

    public Date getDateFromString(String dateTime) {
        String obDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(obDateTimeFormat, Locale.ENGLISH);
        Date eDateTime = null;
        try {
            eDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date(eDateTime.getTime() + TimeZone.getDefault().getOffset(
                System.currentTimeMillis()));
    }

    public boolean isPastEvent(String endDate){
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

}
