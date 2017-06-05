package com.merakiphi.idiotbox.other;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anuragmaravi on 12/04/17.
 */

public class DateFormatter {

    private static DateFormatter dateFormatter;
    private Context context;

    public DateFormatter(Context context) {
        this.context = context;
    }

    public static synchronized DateFormatter getInstance(Context context) {
        if (dateFormatter == null) {
            dateFormatter = new DateFormatter(context);
        }
        return dateFormatter;
    }

    public String formatDate(String dateString) throws ParseException {
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
        Date date = df.parse(dateString);
        return String.valueOf(df2.format(date));
    }
}
