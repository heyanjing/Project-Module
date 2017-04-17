package com.he.module.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class Dates {

    public static final String FORMAT_DATE                  = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME              = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_T_TIME           = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_DATETIME_SSS          = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_T_TIME_SSS       = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String FORMAT_ISO                   = "yyyy-MM-dd'T'HH:mm:ss'Z'";                                        // ISO8601
    public static final String FORMAT_ISO_SSS               = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // ISO8601.SSS
    public static final String FORMAT_NONE                  = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_DATETIME_NONE_SSS     = "yyyyMMddHHmmss";
    /**
     * Date对象自带toString的格式
     */
    public static final String FORMAT_DATE_TOSTRING_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static final String DEFAULT_FORMAT               = FORMAT_DATETIME;

    public static Date newDate() {
        return new Date();
    }

    public static Date newDate(long date) {
        return new Date(date);
    }

    public static Date newDate(int year, int month, int day) {
        return newLocalDate(year, month, day).toDate();
    }

    /**
     * 根据指定的格式转换字符串为Date类型
     */
    public static Date newDate(String date, String format) {
        Date d = null;
        try {
            if (format == null) {
                if (isFormat(date, FORMAT_DATE_TOSTRING_DEFAULT)) {
                    format = FORMAT_DATE_TOSTRING_DEFAULT;
                } else if (isFormat(date, FORMAT_DATE)) {
                    format = FORMAT_DATE;
                } else if (isFormat(date, FORMAT_DATETIME) || isFormat(date, FORMAT_DATE_T_TIME)) {
                    if (Strings.isContains(date, "T")) {
                        format = FORMAT_DATE_T_TIME;
                    } else {
                        format = FORMAT_DATETIME;
                    }
                } else if (isFormat(date, FORMAT_DATETIME_SSS) || isFormat(date, FORMAT_DATE_T_TIME_SSS)) {
                    if (Strings.isContains(date, "T")) {
                        format = FORMAT_DATE_T_TIME_SSS;
                    } else {
                        format = FORMAT_DATETIME_SSS;
                    }
                } else if (isFormat(date, FORMAT_ISO)) {
                    format = FORMAT_ISO;
                } else if (isFormat(date, FORMAT_ISO_SSS)) {
                    format = FORMAT_ISO_SSS;
                } else {
                    format = FORMAT_NONE;
                }
            }
            d = newDateFormat(format, Locale.ENGLISH).parse(date);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return d;
    }

    public static Date newDate(String date) {
        return newDate(date, null);
    }

    public static Date newDateTime(String date) {
        return newDate(date, FORMAT_DATETIME);
    }

    public static LocalDate newLocalDate() {
        return new LocalDate();
    }

    public static LocalDate newLocalDate(String date) {
        return newLocalDate(newDate(date));
    }

    public static LocalDate newLocalDate(String date, String format) {
        return newLocalDate(newDate(date, format));
    }

    public static LocalDate newLocalDate(Date date) {
        return new LocalDate(date);
    }

    public static LocalDate newLocalDate(int year, int month, int day) {
        return new LocalDate(year, month, day);
    }

    public static Date newDateTime(int year, int month, int day, int hour, int minute, int second) {
        return newLocalDateTime(year, month, day, hour, minute, second).toDate();
    }

    public static LocalDateTime newLocalDateTime(int year, int month, int day, int hour, int minute, int second) {
        return new LocalDateTime(year, month, day, hour, minute, second);
    }

    public static LocalDateTime newLocalDateTime(String date) {
        return newLocalDateTime(newDate(date));
    }

    public static LocalDateTime newLocalDateTime(String date, String format) {
        return newLocalDateTime(newDate(date, format));
    }

    public static LocalDateTime newLocalDateTime(Date date) {
        return new LocalDateTime(date);
    }

    public static SimpleDateFormat newDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static SimpleDateFormat newDateFormat(String format, Locale locale) {
        return new SimpleDateFormat(format, locale);
    }

    public static SimpleDateFormat newDateFormatForDate() {
        return newDateFormat(FORMAT_DATE);
    }

    public static SimpleDateFormat newDateFormatForDateTime() {
        return newDateFormat(FORMAT_DATETIME);
    }

    public static SimpleDateFormat newDateFormatForDateISO() {
        return newDateFormat(FORMAT_ISO);
    }

    public static String newDateString(String pattern) {
        return toString(newDate(), pattern);
    }

    public static String newDateStringOfFormatDate() {
        return toString(newDate(), FORMAT_DATE);
    }

    public static String newDateStringOfFormatDateTime() {
        return toString(newDate(), FORMAT_DATETIME);
    }

    public static String newDateStringOfFormatDateISO() {
        return toString(newDate(), FORMAT_ISO);
    }

    public static String newDateStringOfFormatNone() {
        return toString(newDate(), FORMAT_NONE);
    }

    /**
     * 取得今天的开始
     */
    public static Date getStartOfToday() {
        return getStartOfTheDate(new Date());
    }

    /**
     * 取得今天的结束
     */
    public static Date getEndOfToday() {
        return getEndOfTheDate(new Date());
    }

    /**
     * 取得指定date当天的开始
     * date:yyyy-mm-dd
     */
    public static Date getStartOfThe(String date, String format) {
        LocalDate dt = newLocalDate(newDate(date, format));
        return dt.toLocalDateTime(new LocalTime(0, 0, 0)).toDate();
    }

    /**
     * 取得指定date当天的结束
     * date:yyyy-mm-dd
     */
    public static Date getEndOfThe(String date, String format) {
        LocalDate d = newLocalDate(newDate(date, format));
        LocalDateTime dt = d.toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定date当天的开始
     * date:yyyy-mm-dd
     */
    public static Date getStartOfTheDate(String date) {
        LocalDate dt = newLocalDate(date);
        return dt.toLocalDateTime(new LocalTime(0, 0, 0)).toDate();
    }

    /**
     * 取得指定date当天的开始
     */
    public static Date getStartOfTheDate(Date date) {
        LocalDate dt = newLocalDate(date);
        return dt.toLocalDateTime(new LocalTime(0, 0, 0)).toDate();
    }

    /**
     * 取得指定date当天的结束
     * date:yyyy-mm-dd
     */
    public static Date getEndOfTheDate(String date) {
        LocalDate d = newLocalDate(date);
        LocalDateTime dt = d.toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定date当天的结束
     */
    public static Date getEndOfTheDate(Date date) {
        LocalDate d = newLocalDate(date);
        LocalDateTime dt = d.toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的开始
     * date:yyyy-mm-dd hh:mm:ss
     */
    public static Date getStartOfTheDateTime(String datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(0, 0, 0));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的开始
     */
    public static Date getStartOfTheDateTime(Date datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(0, 0, 0));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的结束
     * date:yyyy-mm-dd hh:mm:ss
     */
    public static Date getEndOfTheDateTime(String datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的结束
     */
    public static Date getEndOfTheDateTime(Date datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String toString(Date date, String format) {
        if (format == null) {
            format = DEFAULT_FORMAT;
        }
        return newDateFormat(format).format(date);
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String toString(Date date) {
        return toString(date, null);
    }

    /**
     * 转换为FORMAT_DATE字符串
     */
    public static String toStringWithDateFormatDate(Date date) {
        return toString(date, FORMAT_DATE);
    }

    /**
     * 转换为FORMAT_DATETIME字符串
     */
    public static String toStringWithDateFormatDateTime(Date date) {
        return toString(date, FORMAT_DATETIME);
    }

    /**
     * 转换为FORMAT_ISO字符串
     */
    public static String toStringWithDateFormatISO(Date date) {
        return toString(date, FORMAT_ISO);
    }

    public static boolean isAfter(Date date, Date when) {
        return date.after(when);
    }

    public static boolean isAfterOfNow(Date date) {
        return isAfter(date, newDate());
    }

    public static boolean isBefore(Date date, Date when) {
        return date.before(when);
    }

    public static boolean isBeforeOfNow(Date date) {
        return isBefore(date, newDate());
    }

    public static boolean isToday(Date date) {
        Date d = newDate();
        return isAfter(date, getStartOfTheDate(d)) && isBefore(date, getEndOfTheDate(d));
    }

    /**
     * 在日期上增加数个整月
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 在现在增加数个整月
     */
    public static Date addMonthOfNow(int n) {
        return addMonth(Dates.newDate(), n);
    }

    /**
     * 在日期上加多少天
     */
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, n);
        return cal.getTime();
    }

    /**
     * 在现在日期上加多少天
     */
    public static Date addDayOfNow(int n) {
        return addDay(newDate(), n);
    }

    /**
     * 在日期上增加数个整分钟
     */
    public static String addMinute(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, n);
        return toStringWithDateFormatDateTime(cal.getTime());
    }

    /**
     * 在现在时间上增加数个整分钟
     */
    public static String addMinuteOfNow(int n) {
        return addMinute(Dates.newDate(), n);
    }

    /**
     * 在日期上增加数个整秒
     */
    public static String addSecond(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, n);
        return toStringWithDateFormatDateTime(cal.getTime());
    }

    /**
     * 在现在增加数个整秒
     */
    public static String addSecond(int n) {
        return addSecond(Dates.newDate(), n);
    }

    public static int getLengthOfFormat(String format) {
        return format.replaceAll("\'", "").length();
    }

    public static boolean isFormat(String date, String format) {
        return date.length() == getLengthOfFormat(format);
    }

}
