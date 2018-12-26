package com.rchat.platform.common;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * 获取指定时间范围的开始时间
     *
     * @param now   时间
     * @param field 范围类型
     * @return 开始时间
     */
    public static Date getStartTime(Calendar now, int field) {
        switch (field) {
            case Calendar.YEAR:
                now.set(Calendar.MONTH, 0);
            case Calendar.MONTH:
                now.set(Calendar.DATE, 1);
            case Calendar.DATE:
                now.set(Calendar.HOUR_OF_DAY, 0);
            case Calendar.HOUR_OF_DAY:
                now.set(Calendar.MINUTE, 0);
            case Calendar.MINUTE:
                now.set(Calendar.SECOND, 0);
            default:
                break;
        }
        return now.getTime();
    }

    /**
     * 指定时间范围的结束时间
     *
     * @param date  日期
     * @param field 时间范围
     * @return 结束时间
     */
    public static Date getEndTime(Calendar date, int field) {
        switch (field) {
            case Calendar.YEAR:
                date.set(Calendar.MONTH, 11);
            case Calendar.MONTH:
                if (Year.isLeap(date.get(Calendar.YEAR))) {
                    date.set(Calendar.DATE, Month.of(date.get(Calendar.MONTH) + 1).maxLength());
                } else {
                    date.set(Calendar.DATE, Month.of(date.get(Calendar.MONTH) + 1).minLength());
                }
            case Calendar.DATE:
                date.set(Calendar.HOUR_OF_DAY, 23);
            case Calendar.HOUR_OF_DAY:
                date.set(Calendar.MINUTE, 59);
            case Calendar.MINUTE:
                date.set(Calendar.SECOND, 59);
            default:
                break;
        }
        return date.getTime();
    }

    /**
     * 今年的开始时间
     *
     * @return 今年一月一日零点
     */
    public static Date currentYearStartTime() {
        return currentStartTime(Calendar.YEAR);
    }

    /**
     * 当月的开始时间
     *
     * @return 当月一日的零点
     */
    public static Date currentMonthStartTime() {
        return currentStartTime(Calendar.MONTH);
    }

    /**
     * 今天的开始时间
     *
     * @return 今天零点
     */
    public static Date currentDateStartTime() {
        return currentStartTime(Calendar.DATE);
    }

    /**
     * 当前这个小时的开始时间
     *
     * @return 当前这个消失的零分零秒
     */
    public static Date currentHourStartTime() {
        return currentStartTime(Calendar.HOUR_OF_DAY);
    }

    public static Date currentMinuteStartTime() {
        return currentStartTime(Calendar.MINUTE);
    }

    /**
     * 今年的结束时间
     * @return 今年
     */
    public static Date currentYearEndTime() {
        return currentEndTime(Calendar.YEAR);
    }

    public static Date currentMonthEndTime() {
        return currentEndTime(Calendar.MONTH);
    }

    public static Date currentDateEndTime() {
        return currentEndTime(Calendar.DATE);
    }

    public static Date currentHourEndTime() {
        return currentEndTime(Calendar.HOUR_OF_DAY);
    }

    public static Date currentMinuteEndTime() {
        return currentEndTime(Calendar.MINUTE);
    }


    public static Date currentStartTime(int filed) {
        return getStartTime(Calendar.getInstance(), filed);
    }

    public static Date currentEndTime(int filed) {
        return getEndTime(Calendar.getInstance(), filed);
    }

    public static Date lastThreeMonthAgo() {
        return lastTimeAgo(Calendar.MONTH, 3);
    }

    public static Date lastMonthAgo(int amount) {
        return lastTimeAgo(Calendar.MONTH, amount);
    }

    public static Date getSeasonStartTime(Calendar date) {
        int season = getSeason(date);

        switch (season) {
            case 1:
                date.set(Calendar.MONTH, Calendar.JANUARY);
                break;
            case 2:
                date.set(Calendar.MONTH, Calendar.APRIL);
                break;
            case 3:
                date.set(Calendar.MONTH, Calendar.JULY);
                break;
            case 4:
                date.set(Calendar.MONTH, Calendar.OCTOBER);
                break;
            default:
                throw new IllegalArgumentException(String.format("指定季度不存在： season = %d", season));
        }
        return getStartTime(date, Calendar.MONTH);
    }

    public static Date currentSeasonStartTime() {
        return getSeasonStartTime(Calendar.getInstance());
    }

    public static Date getSeasonEndTime(Calendar date) {
        int season = getSeason(date);

        switch (season) {
            case 1:
                date.set(Calendar.MONTH, Calendar.MARCH);
                break;
            case 2:
                date.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case 3:
                date.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case 4:
                date.set(Calendar.MONTH, Calendar.DECEMBER);
                break;
            default:
                throw new IllegalArgumentException(String.format("指定季度不存在： season = %d", season));
        }
        return getEndTime(date, Calendar.MONTH);
    }

    public static Date currentSeasonEndTime() {
        return getSeasonEndTime(Calendar.getInstance());
    }

    /**
     * 得到指定日期的季度
     *
     * @param date 日期
     * @return 季度
     */
    public static int getSeason(Calendar date) {
        int season = 0;
        int month = date.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }

    public static Date lastTimeAgo(int filed, int amount) {
        Calendar now = Calendar.getInstance();
        now.add(filed, -amount);
        return now.getTime();
    }

    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(currentYearStartTime()));
        System.out.println(format.format(currentYearEndTime()));

        System.out.println(format.format(currentMonthStartTime()));
        System.out.println(format.format(currentMonthEndTime()));

        System.out.println(format.format(currentDateStartTime()));
        System.out.println(format.format(currentDateEndTime()));

        System.out.println(format.format(currentHourStartTime()));
        System.out.println(format.format(currentHourEndTime()));

        System.out.println(format.format(currentMinuteStartTime()));
        System.out.println(format.format(currentMinuteEndTime()));

        System.out.println(format.format(currentSeasonStartTime()));
        System.out.println(format.format(currentSeasonEndTime()));
    }

}
