package org.onebrick.android.helpers;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rush on 10/13/14.
 */
public class Utils {
    private static final String TAG = "Utils";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFormattedTime(String input){
        Date date = null;
        try {
            date = dateFormat.parse(input);
            input = new SimpleDateFormat("yyyy-MM-dd H:mm").format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input;
    }
    public static String getFormattedTimeDateOnly(String input){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input);
            input = new SimpleDateFormat("MM-dd EEE").format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return input;
    }
    public static String getFormattedTimeEndOnly(String start, String end){
        Date startDate = null;
        Date endDate = null;
        String endTime = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start);
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);

            String sStart = new SimpleDateFormat("MM-dd").format(startDate);
            String eStart = new SimpleDateFormat("MM-dd").format(endDate);
            if (sStart.equals(eStart)){
                endTime = new SimpleDateFormat("HH:mm").format(endDate);
            }else{
                endTime = getFormattedTime(end);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
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

}
