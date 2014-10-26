package org.onebrick.android.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AshwinGV on 10/19/14.
 */
public class DateTimeFormatter {
    private static DateTimeFormatter dtf;
    private DateTimeFormatter() {};

    public static DateTimeFormatter getInstance() {
        if(dtf == null) {
            dtf = new DateTimeFormatter();
        }
        return dtf;
    }

    public String formatDateTime(String dateTime) {
        String formattedDateTime = null;
        String obDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sf = new SimpleDateFormat(obDateTimeFormat, Locale.ENGLISH);
        SimpleDateFormat obDateFormat = new SimpleDateFormat("EEE, MMM dd KK:mm a");
        Date eDateTime;
        try {
            eDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
            formattedDateTime = ""+obDateFormat.format(eDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDateTime;
    }

    public String formatDateTimeEndDateOnly(String start, String end){
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
                endTime = formatDateTime(end);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
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
        return eDateTime;
    }

}
