package com.zousan.homesecurity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Locale;

/**
 * Created by zousan on 2015/03/15.
 */
public class Utils {
    private Utils(){

    }

    public static String timeConverter(Date date){
        final DateTime dateTime = new DateTime(date);
        final DateTimeFormatter fullTime = DateTimeFormat.fullDateTime();
        return fullTime.withLocale(Locale.JAPANESE).print(dateTime);
    }
}
