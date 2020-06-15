package com.sfwl.bh.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/8 16:42
 */
public class DateUtil {

    public static final long MILLS_PER_SECOND = 1000;
    public static final long MILLS_PER_MINUTE = 60 * MILLS_PER_SECOND;
    public static final long MILLS_PER_HOUR = 60 * MILLS_PER_MINUTE;
    public static final long MILLS_PER_DAY = 24 * MILLS_PER_HOUR;
    public static final long MILLS_PER_WEEK = 7 * MILLS_PER_DAY;

    public static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_3 = "yyyy-MM";
    public static final String DATE_FORMAT_4 = "yyyyMMdd";
    public static final String DATE_FORMAT_5 = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_6 = "yyyyMMddHHmmssSSS";

    /**
     * 将时间文本转成日期格式
     *
     * @param dateStr 时间文本
     * @param format  格式化文本
     * @return
     * @throws ParseException
     */
    public static Date parseDate(final String dateStr, final String format) throws ParseException {
        if (StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(format)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        }
        return null;
    }

    /**
     * 将时间文本转成日期格式
     *
     * @param dateStr 时间文本
     *                yyyy-MM-dd HH:mm:ss
     * @return
     * @throws ParseException
     */
    public static Date ymdhmsStr2Date(final String dateStr) throws ParseException {
        return parseDate(dateStr, DATE_FORMAT_1);
    }

    /**
     * 将时间文本转成日期格式
     *
     * @param dateStr 时间文本
     *                yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static Date ymdStr2Date(final String dateStr) throws ParseException {
        return parseDate(dateStr, DATE_FORMAT_2);
    }

    /**
     * 将日期转成指定格式文本
     *
     * @param date   日期
     * @param format 格式化文本
     * @return
     */
    public static String formatDate(final Date date, final String format) {
        if (date != null && StringUtils.isNotBlank(format)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        return null;
    }

    /**
     * 用将日期转成指定格式文本
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     * @return
     */
    public static String date2ymdhmsStr(final Date date) {
        return formatDate(date, DATE_FORMAT_1);
    }

    /**
     * 用将日期转成指定格式文本
     * yyyy-MM-dd
     *
     * @param date 日期
     * @return
     */
    public static String date2ymdStr(final Date date) {
        return formatDate(date, DATE_FORMAT_2);
    }

    /**
     * 用将日期转成指定格式文本
     * yyyy-MM
     *
     * @param date 日期
     * @return
     */
    public static String date2ymStr(final Date date) {
        return formatDate(date, DATE_FORMAT_3);
    }

    /**
     * 获取指定日期增加秒数后的日期
     *
     * @param date
     * @param seconds
     * @return
     */
    public static Date addSeconds(final Date date, final int seconds) {
        return new Date(date.getTime() + seconds * MILLS_PER_SECOND);
    }

    /**
     * 获取增加秒数后的日期
     *
     * @param seconds
     * @return
     */
    public static Date addSeconds(final int seconds) {
        return new Date(System.currentTimeMillis() + seconds * MILLS_PER_SECOND);
    }

    /**
     * 获取指定日期增加分数后的日期
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date addMinutes(final Date date, final int minutes) {
        return new Date(date.getTime() + minutes * MILLS_PER_MINUTE);
    }

    /**
     * 获取增加分数后的日期
     *
     * @param minutes
     * @return
     */
    public static Date addMinutes(final int minutes) {
        return new Date(System.currentTimeMillis() + minutes * MILLS_PER_MINUTE);
    }

    /**
     * 获取指定日期增加小时后的日期
     *
     * @param date
     * @param hours
     * @return
     */
    public static Date addHours(final Date date, final int hours) {
        return new Date(date.getTime() + hours * MILLS_PER_HOUR);
    }

    /**
     * 获取增加小时后的日期
     *
     * @param hours
     * @return
     */
    public static Date addHours(final int hours) {
        return new Date(System.currentTimeMillis() + hours * MILLS_PER_HOUR);
    }

    /**
     * 获取指定日期增加天数后的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(final Date date, final int days) {
        return new Date(date.getTime() + days * MILLS_PER_DAY);
    }

    /**
     * 获取增加天数后的日期
     *
     * @param days
     * @return
     */
    public static Date addDays(final int days) {
        return new Date(System.currentTimeMillis() + days * MILLS_PER_DAY);
    }

    /**
     * 获取指定日期增加周数后的日期
     *
     * @param date
     * @param weeks
     * @return
     */
    public static Date addWeeks(final Date date, final int weeks) {
        return DateUtils.addWeeks(date, weeks);
    }

    /**
     * 获取指定日期增加周数后的日期
     *
     * @param weeks
     * @return
     */
    public static Date addWeeks(final int weeks) {
        return DateUtils.addWeeks(new Date(), weeks);
    }

    /**
     * 获取指定日期增加月数后的日期
     *
     * @param date
     * @param months
     * @return
     */
    public static Date addMonths(final Date date, final int months) {
        return DateUtils.addMonths(date, months);
    }

    /**
     * 获取增加月数后的日期
     *
     * @param months
     * @return
     */
    public static Date addMonths(final int months) {
        return DateUtils.addMonths(new Date(), months);
    }

    /**
     * 获取指定日期增加年数后的日期
     *
     * @param date
     * @param years
     * @return
     */
    public static Date addYears(final Date date, final int years) {
        return DateUtils.addYears(date, years);
    }

    /**
     * 获取增加年数后的日期
     *
     * @param years
     * @return
     */
    public static Date addYears(final int years) {
        return DateUtils.addYears(new Date(), years);
    }

    /**
     * 判断两个日期是否同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isTheSameDay(final Date date1, final Date date2) {
        return DateUtils.isSameDay(date1, date2);
    }

    /**
     * 判断一个日期是否当天
     *
     * @param date
     * @return
     */
    public static boolean isTheDay(final Date date) {
        return isTheSameDay(new Date(), date);
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date monthBegin() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }
}
