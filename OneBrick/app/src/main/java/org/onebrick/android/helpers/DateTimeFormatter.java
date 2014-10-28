package org.onebrick.android.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by AshwinGV on 10/19/14.
 */
public class DateTimeFormatter {
    private static DateTimeFormatter dtf;
    private DateTimeFormatter() {};
    private static Calendar cal = Calendar.getInstance();
    private static TimeZone tz = cal.getTimeZone();

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

    public String getDateOnly(Date d) {
        String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEE", d);
        String month = (String) android.text.format.DateFormat.format("MMM", d);
        String  day = (String) android.text.format.DateFormat.format("dd", d);
        String thisYear = ""+cal.get(Calendar.YEAR);
        String eventYear = (String) android.text.format.DateFormat.format("yyyy", d);
        StringBuffer toReturn = new StringBuffer();
        toReturn.append(dayOfTheWeek);
        toReturn.append(", ");
        toReturn.append(month);
        toReturn.append(" ");
        toReturn.append(day);
        if(!thisYear.equalsIgnoreCase(eventYear)){
            toReturn.append(", ");
            toReturn.append(eventYear);
        }
        return  toReturn.toString();
    }

    public String getTimeOnly(Date d) {
        String time =  (String) android.text.format.DateFormat.format("KK:mm a", d);
        return time;
    }

}
