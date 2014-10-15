package org.onebrick.android.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rush on 10/13/14.
 */
public class Utils {
    public static String getFormattedTime(String input){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input);
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

}
