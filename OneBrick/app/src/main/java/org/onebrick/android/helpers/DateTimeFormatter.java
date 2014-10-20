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
