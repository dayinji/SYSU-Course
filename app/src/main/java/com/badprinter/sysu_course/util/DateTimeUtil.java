package com.badprinter.sysu_course.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by root on 15-9-14.
 */
public class DateTimeUtil {
    public static String getCurrentTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.CHINA);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
