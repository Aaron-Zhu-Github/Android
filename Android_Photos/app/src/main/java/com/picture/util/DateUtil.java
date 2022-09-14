package com.picture.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private DateUtil() {
    }

    public static String getDateByRecentDay(String startDate, int recentDay) {
        int year = Integer.parseInt(startDate.substring(0, 4));
        int month = Integer.parseInt(startDate.substring(5, 7)) + 1;
        int day = Integer.parseInt(startDate.substring(8, 10));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.DATE, recentDay);

        String date = cal.get(Calendar.YEAR) + "/" + StringUtil.padLeft(String.valueOf(cal.get(Calendar.MONTH) - 1), 2)
                + "/" + StringUtil.padLeft(String.valueOf(cal.get(Calendar.DATE)), 2);
        return date;
    }

    public static String convertToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        return df.format(date);
    }

    public static Date convertToDate(String strDate) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        return df.parse(strDate);
    }

    public static String convertToString(long timeMillion) {
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        return df.format(timeMillion);
    }

    public static String formatYMD(String year, String month, String day) {
        return year + "/" + month + "/" + day + "/";
    }

    public static String formatTime(long time) {
        SimpleDateFormat aa = new SimpleDateFormat("aa KK:mm", Locale.CHINESE);
        Date d = null;
        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            d = df.parse(convertToString(time));
        } catch (ParseException e) {
        }
        return aa.format(d);
    }

    public static String nowFormat() {
        return format(now(), "yyyyMMdd_HHmmss");
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static String format(Object o, String pattern) {
        if (o instanceof Date) {
            DateFormat df = new SimpleDateFormat(pattern);
            return df.format((Date) o);
        } else if (o instanceof Calendar) {
            DateFormat df = new SimpleDateFormat(pattern);
            return df.format(((Calendar) o).getTime());
        } else if (o instanceof String) {
            DateFormat df = new SimpleDateFormat(pattern);
            return df.format((stringToCalendar(String.valueOf(o))).getTime());
        }
        return "";
    }

    public static Calendar stringToCalendar(String str) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(stringToDate(str));
        return cal;
    }

    public static Date stringToDate(String str) {
        final String[] fmt = {"yyyy-MM-dd", "yyyyMMddHHmmss", "yyyyMMdd_HHmmss"};
        for (String element : fmt) {
            DateFormat df = new SimpleDateFormat(element);
            try {
                Date d = df.parse(str);
                return d;
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
