package com.hwtx.form.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class DateUtil {

    public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final int DATE_PART_MONTH = Calendar.MONTH;
    public static final int DATE_PART_DATE = Calendar.DATE;
    public static final int DATE_PART_SECOND = Calendar.SECOND;


    private static final Object calendarLock = new Object();
    private static final Map<String, ThreadLocal<Calendar>> calendars = new HashMap<>();

    public static Calendar getCalendar(TimeZone zone, Locale local) {
        if (null == zone) {
            zone = TimeZone.getTimeZone("Asia/Shanghai");
        }
        if (null == local) {
            local = Locale.CHINESE;
        }
        final TimeZone _zone = zone;
        final Locale _local = local;
        String key = zone.getDisplayName() + local.getDisplayName();
        ThreadLocal<Calendar> instance = calendars.get(key);
        if (instance == null) {
            synchronized (calendarLock) {
                instance = calendars.get(key);
                if (instance == null) {
                    instance = ThreadLocal.withInitial(() -> Calendar.getInstance(_zone, _local));
                    calendars.put(key, instance);
                }
            }
        }
        return instance.get();
    }

    private static Calendar getCalendar() {
        return getCalendar(null, null);
    }

    /**
     * cur是否在fr与to之间(包含fr, to)
     *
     * @param cur 时间
     * @param fr  开始时间
     * @param to  结束时间
     * @return boolean
     */
    public static boolean between(Date cur, Date fr, Date to) {
        if (cur.getTime() >= fr.getTime() && cur.getTime() <= to.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * cur是否在fr与to之间(包含fr, to)
     *
     * @param cur 时间
     * @param fr  开始时间
     * @param to  结束时间
     * @return boolean
     */
    public static boolean between(String cur, String fr, String to) {
        return between(parse(cur), parse(fr), parse(to));
    }

    /**
     * 时间差
     *
     * @param part 参考Calendar
     * @param fr   开始时间
     * @param to   结束时间
     * @return long
     */
    public static long diff(int part, Date fr, Date to) {
        long result = 0;
        Calendar calendar = getCalendar();
        if (Calendar.YEAR == part) {
            calendar.setTime(to);
            int time = calendar.get(Calendar.YEAR);
            calendar.setTime(fr);
            result = time - calendar.get(Calendar.YEAR);
        }
        if (Calendar.MONTH == part) {
            calendar.setTime(to);
            int time = calendar.get(Calendar.YEAR) * 12;
            calendar.setTime(fr);
            time -= calendar.get(Calendar.YEAR) * 12;
            calendar.setTime(to);
            time += calendar.get(Calendar.MONTH);
            calendar.setTime(fr);
            result = time - calendar.get(Calendar.MONTH);
        }
        if (Calendar.WEEK_OF_YEAR == part) {
            calendar.setTime(to);
            int time = calendar.get(Calendar.YEAR) * 52;
            calendar.setTime(fr);
            time -= calendar.get(Calendar.YEAR) * 52;
            calendar.setTime(to);
            time += calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(fr);
            result = time - calendar.get(Calendar.WEEK_OF_YEAR);
        }
        long ms = to.getTime() - fr.getTime();
        if (Calendar.DAY_OF_YEAR == part || Calendar.DATE == part) {
            result = ms / 1000 / 60 / 60 / 24;
        } else if (Calendar.HOUR == part) {
            result = ms / 1000 / 60 / 60;
        } else if (Calendar.MINUTE == part) {
            result = ms / 1000 / 60;
        } else if (Calendar.SECOND == part) {
            result = ms / 1000;
        } else if (Calendar.MILLISECOND == part) {
            result = ms;
        }
        return result;
    }

    public static long diff(int part, String fr, String to) {
        return diff(part, parse(fr), parse(to));
    }

    public static long diff(int part, Date fr) {
        return diff(part, fr, new Date());
    }

    public static long diff(int part, String fr) {
        return diff(part, parse(fr));
    }

    /**
     * 格式化日期
     *
     * @param locale 地区/语言, 格式化月份, 星期几时根据地区语言,
     *               如MMMMM(zh:一月, en:January)MMMM(zh:一月, en:Jan) EEE(zh:星期五, en:Fri) EEEE(en:Friday)
     * @param date   日期
     * @param format 格式
     * @return String
     */
    public static String format(Locale locale, ZoneId zone, Date date, String format) {
        if (null == date || null == format)
            return null;
        LocalDateTime datetime = localDateTime(date, zone);
        return datetime.format(DateTimeFormatter.ofPattern(format, locale));
    }

    public static String format(Locale locale, ZoneId zone, Long date, String format) {
        if (null == date || null == format) {
            return null;
        }
        return format(locale, zone, parse(date), format);
    }

    public static String format(Locale locale, ZoneId zone) {
        return format(locale, zone, new Date(), FORMAT_DATE_TIME);
    }

    public static String format(Locale locale, ZoneId zone, String format) {
        return format(locale, zone, new Date(), format);
    }

    public static String format(Locale locale, ZoneId zone, Date date) {
        return format(locale, zone, date, FORMAT_FULL);
    }

    public static String format(Locale locale, ZoneId zone, Long date) {
        return format(locale, zone, date, FORMAT_FULL);
    }

    public static String format(Locale locale, ZoneId zone, String date, String format) {
        Date d = parse(date);
        return format(locale, zone, d, format);
    }

    public static String format(Locale locale, Date date, String format) {
        return format(locale, ZoneId.systemDefault(), date, format);
    }

    public static String format(Locale locale, Long date, String format) {
        return format(locale, ZoneId.systemDefault(), date, format);
    }

    public static String format(Locale locale) {
        return format(locale, ZoneId.systemDefault());
    }

    public static String format(Locale locale, String format) {
        return format(locale, ZoneId.systemDefault(), format);
    }

    public static String format(Locale locale, Date date) {
        return format(locale, ZoneId.systemDefault(), date);
    }

    public static String format(Locale locale, Long date) {
        return format(locale, ZoneId.systemDefault(), date);
    }

    public static String format(Locale locale, String date, String format) {
        return format(locale, ZoneId.systemDefault(), date, format);
    }

    public static String format(Date date, String format) {
        return format(Locale.getDefault(), ZoneId.systemDefault(), date, format);
    }

    public static String format(Long date, String format) {
        return format(Locale.getDefault(), ZoneId.systemDefault(), date, format);
    }

    public static String format() {
        return format(Locale.getDefault(), ZoneId.systemDefault());
    }

    public static String format(String format) {
        return format(Locale.getDefault(), ZoneId.systemDefault(), format);
    }

    public static String format(Date date) {
        return format(Locale.getDefault(), ZoneId.systemDefault(), date);
    }

    public static String format(Long date) {
        return format(Locale.getDefault(), ZoneId.systemDefault(), date);
    }

    public static String format(String date, String format) {
        return format(Locale.getDefault(), ZoneId.systemDefault(), date, format);
    }

    /**
     * 分钟转换成时间
     *
     * @param minute 分钟(610=10:10)
     * @return String
     */
    public static String convertMinute(int minute) {
        String time = "";
        int h = minute / 60;
        int m = minute % 60;
        if (h < 10) {
            time += "0";
        }
        time += h + ":";
        if (m < 10) {
            time += "0";
        }
        time += m;
        return time;
    }

    /**
     * 星期几
     *
     * @param date 日期
     * @return String
     */
    public static String getWeek(Date date) {
        return format(date, "EEEE");
    }

    /**
     * 当周第一天
     * 周日作为一周的第一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);// 设为当前周的第一天
        return calendar.getTime();
    }

    public static Date getFirstDayOfWeek() {
        return getFirstDayOfWeek(new Date());
    }

    /**
     * 下周第一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfNextWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);// 减一个周
        calendar.set(Calendar.DAY_OF_WEEK, 1);// 把日期设置为当周第一天
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextWeek() {
        return getFirstDayOfNextWeek(new Date());
    }


    /**
     * 上个周第一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfPreviousWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);// 减一个周
        calendar.set(Calendar.DAY_OF_WEEK, 1);// 设为当前周第一天
        return calendar.getTime();
    }

    public static Date getFirstDayOfPreviousWeek() {
        return getFirstDayOfPreviousWeek(new Date());
    }

    /**
     * 当周最后天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return calendar.getTime();
    }

    public static Date getLastDayOfWeek() {
        return getLastDayOfWeek(new Date());
    }

    /**
     * 下周最后天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getLastDayOfNextWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);// 减一个周
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return calendar.getTime();
    }

    public static Date getLastDayOfNextWeek() {
        return getLastDayOfNextWeek(new Date());
    }


    /**
     * 上个周最后天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getLastDayOfPreviousWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);// 减一个周
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return calendar.getTime();
    }

    public static Date getLastDayOfPreviousWeek() {
        return getLastDayOfPreviousWeek(new Date());
    }

    /**
     * 当月第一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设为当前月的1号
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonth() {
        return getFirstDayOfMonth(new Date());
    }

    /**
     * 下个月第一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfNextMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);// 减一个月
        calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextMonth() {
        return getFirstDayOfNextMonth(new Date());
    }

    /**
     * 上个月第一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfPreviousMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);// 设为当前月的1号
        calendar.add(Calendar.MONTH, -1);// 减一个月, 变为下月的1号
        return calendar.getTime();
    }

    public static Date getFirstDayOfPreviousMonth() {
        return getFirstDayOfPreviousMonth(new Date());
    }

    /**
     * 当月最后一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(date.getTime() + 100000);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设为当前月的1号
        calendar.add(Calendar.MONTH, 1);// 加一个月, 变为下月的1号
        calendar.add(Calendar.DATE, -1);// 减去一天, 变为当月最后一天
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth() {
        return getLastDayOfMonth(new Date());
    }

    /**
     * 上月最后一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getLastDayOfPreviousMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);// 减一个月
        calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1);// 日期回滚一天, 也就是本月最后一天
        return calendar.getTime();
    }

    public static Date getLastDayOfPreviousMonth() {
        return getLastDayOfPreviousMonth(new Date());
    }

    /**
     * 下月最后一天
     *
     * @param date 日期
     * @return Date
     */
    public static Date getLastDayOfNextMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);// 加一个月
        calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1);// 日期回滚一天, 也就是本月最后一天
        return calendar.getTime();
    }

    public static Date getLastDayOfNextMonth() {
        return getLastDayOfNextMonth(new Date());
    }

    // 本周星期日的日期
    public static Date getCurrentWeekday(Date date) {
        Calendar calendar = getCalendar();
        int mondayPlus = getMondayPlus(date);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, mondayPlus + 6);
        return calendar.getTime();
    }

    public static Date getCurrentWeekday() {
        return getCurrentWeekday(new Date());
    }

    // 当前日期与本周日相差的天数
    public static int getMondayPlus(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        // 今天是一周的第几天, 星期日是第一天, 星期二是第二天......
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    public static int getMondayPlus() {
        return getMondayPlus(new Date());
    }

    // 本周一的日期
    public static Date getMondayOFWeek(Date date) {
        Calendar calendar = getCalendar();
        int mondayPlus = getMondayPlus(date);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, mondayPlus);
        return calendar.getTime();
    }

    public static Date getMondayOFWeek() {
        return getMondayOFWeek(new Date());
    }

    // 下周星期一的日期
    public static Date getNextMonday(Date date) {
        Calendar calendar = getCalendar();
        int mondayPlus = getMondayPlus(date);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, mondayPlus + 7);
        return calendar.getTime();
    }

    public static Date getNextMonday() {
        return getNextMonday(new Date());
    }

    // 下周星期日的日期
    public static Date getNextSunday(Date date) {
        Calendar calendar = getCalendar();
        int mondayPlus = getMondayPlus(date);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, mondayPlus + 7 + 6);
        return calendar.getTime();
    }

    public static Date getNextSunday() {
        return getNextSunday(new Date());
    }

    // 当前日期与本周日相差几天
    public static int getMonthPlus(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        int monthOfNumber = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1);// 日期回滚一天, 也就是最后一天
        // 一月最大天数
        int maxDate = calendar.get(Calendar.DATE);
        if (monthOfNumber == 1) {
            return -maxDate;
        } else {
            return 1 - monthOfNumber;
        }
    }

    /**
     * 明年最后一天的日期
     *
     * @param date 日期
     * @return Date
     */
    public static Date getNextYearEnd(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1);// 加一个年
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static Date getNextYearEnd() {
        return getNextYearEnd(new Date());
    }

    /**
     * 明年第一天的日期
     *
     * @param date 日期
     * @return Date
     */
    public static Date getNextYearFirst(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1);// 加一个年
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    public static Date getNextYearFirst() {
        return getNextYearFirst(new Date());
    }

    /**
     * 一年多少天
     *
     * @param date date
     * @return int
     */
    public static int countDaysOfYear(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
        calendar.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天.
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int countDaysOfYear(int year) {
        Date date = parse(year + "-01-01");
        return countDaysOfYear(date);
    }

    public static int countDaysOfYear() {
        return countDaysOfYear(new Date());
    }

    /**
     * 一年多少天
     *
     * @param date date
     * @return int
     */
    public static int countDaysOfMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // 把时间调整为当月的第一天;
        calendar.add(Calendar.MONTH, 1); // 月份调至下个月;
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 时间减去一天（就等于上个月的最后一天）
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int countDaysOfMonth() {
        return countDaysOfMonth(new Date());
    }

    private static int getYearPlus(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        int yearOfNumber = calendar.get(Calendar.DAY_OF_YEAR);// 当天是一年中的第几天
        calendar.set(Calendar.DAY_OF_YEAR, 1);// 把日期设为当年第一天
        calendar.roll(Calendar.DAY_OF_YEAR, -1);// 把日期回滚一天.
        int MaxYear = calendar.get(Calendar.DAY_OF_YEAR);
        if (yearOfNumber == 1) {
            return -MaxYear;
        } else {
            return 1 - yearOfNumber;
        }
    }


    /**
     * 本年第一天的日期
     *
     * @param date 日期
     * @return Date
     */
    public static Date getFirstDayOfYear(Date date) {
        Calendar calendar = getCalendar();
        int yearPlus = getYearPlus(date);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, yearPlus);
        return calendar.getTime();
    }

    public static Date getFirstDayOfYear() {
        return getFirstDayOfYear(new Date());
    }

    /**
     * 本年最后一天的日期
     *
     * @param date 日期
     * @return String
     */
    public static String getCurrentYearEnd(Date date) {
        return format(date, "yyyy") + "-12-31";
    }

    public static String getCurrentYearEnd() {
        return getCurrentYearEnd(new Date());
    }


    /**
     * 上年第一天的日期
     *
     * @param date 日期
     * @return String
     */
    public static String getPreviousYearFirst(Date date) {
        String years = format(date, "yyyy");
        return Integer.parseInt(years) - 1 + "-01-01";
    }

    public static String getPreviousYearFirst() {
        return getPreviousYearFirst(new Date());
    }

    /**
     * 获取某年某月的最后一天
     *
     * @param year  年
     * @param month 月
     * @return 最后一天
     */
    public static int getLastDayOfMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        }
        return 0;
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return boolean
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 转换成日期
     *
     * @param date   日期
     * @param format format
     * @return Date
     */
    public static Date parse(String date, String format) throws RuntimeException {
        DateTimeFormatter formatter;
        if (null != format || format.isEmpty()) {
            formatter = DateTimeFormatter.ofPattern(format);
        } else {
            formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        }
        if (format.toUpperCase().contains("HH")) {
            if (format.toUpperCase().contains("D")) {
                return parse(LocalDateTime.parse(date, formatter));
            } else {
                return parse(LocalTime.parse(date, formatter));
            }
        } else {
            return parse(LocalDate.parse(date, formatter));
        }
    }

    public static Date parse(Long value) {
        return new Date(value);
    }

    public static Date parse(java.sql.Date value) {
        return new Date(value.getTime());
    }

    public static Date parse(java.sql.Time value) {
        return new Date(value.getTime());
    }

    public static Date parse(LocalDate value) {
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date parse(LocalDateTime value) {
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date parse(LocalTime value) {
        return Date.from(value.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date parse(Timestamp value) {
        return new Date(value.getTime());
    }

    public static Date parse(Object value) throws RuntimeException {
        Date date = null;
        if (null != value) {
            if (value.getClass() == Date.class) {
                date = (Date) value;
            } else if (value instanceof Timestamp) {
                date = parse((Timestamp) value);
            } else if (value instanceof java.sql.Date) {
                date = parse((java.sql.Date) value);
            } else if (value instanceof java.sql.Time) {
                date = parse((java.sql.Time) value);
            } else if (value instanceof Long) {
                date = parse((Long) value);
            } else if (value instanceof LocalDate) {
                date = parse((LocalDate) value);
            } else if (value instanceof LocalTime) {
                date = parse((LocalTime) value);
            } else if (value instanceof LocalDateTime) {
                date = parse((LocalDateTime) value);
            } else if (value instanceof String) {
                if (BasicUtil.isNumber(value)) {
                    Long timestamp = BasicUtil.parseLong(value, 0L);
                    date = parse(timestamp);
                } else {
                    throw new UnsupportedOperationException("不支持转化为Date类型，input = " + value);
                }
            }
        }
        return date;
    }

    /**
     * 昨天
     *
     * @return Date
     */
    public static Date yesterday() {
        return addDay(-1);
    }

    /**
     * 明天
     *
     * @return Date
     */
    public static Date tomorrow() {
        return addDay(1);
    }

    /**
     * 现在
     *
     * @return Date
     */
    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 按日加
     *
     * @param value 天数
     * @return Date
     */
    public static Date addDay(int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, value);
        return calendar.getTime();
    }

    /**
     * 按日加, 指定日期
     *
     * @param date  日期
     * @param value 天数
     * @return Date
     */
    public static Date addDay(Date date, int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, value);
        return calendar.getTime();
    }

    public static String addDay(String date, int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(parse(date));
        calendar.add(Calendar.DAY_OF_YEAR, value);
        return format(calendar.getTime(), "yyyy-MM-dd");
    }

    /**
     * 按月加
     *
     * @param value 月数
     * @return Date
     */
    public static Date addMonth(int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, value);
        return calendar.getTime();
    }

    /**
     * 按月加, 指定日期
     *
     * @param date  日期
     * @param value 月数
     * @return Date
     */
    public static Date addMonth(Date date, int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, value);
        return calendar.getTime();
    }

    public static Date addMonth(String date, int value) {
        return addMonth(parse(date), value);
    }

    /**
     * 按年加
     *
     * @param value 年数
     * @return Date
     */
    public static Date addYear(int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, value);
        return calendar.getTime();
    }

    /**
     * 当前日期所在周的第idx天 第1天:星期日 第7天:星期六
     *
     * @param idx  idx
     * @param date 日期
     * @return Date
     */
    public static Date getDateOfWeek(int idx, Date date) {
        Date result = null;
        Calendar cal = getCalendar();
        cal.setTime(date);
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - idx;
        cal.add(Calendar.DATE, -day_of_week);
        result = cal.getTime();
        return result;
    }

    /**
     * 星期几(礼拜几)
     *
     * @param date date
     * @return int
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getDayOfWeek() {
        return getDayOfWeek(new Date());
    }

    public static int getDayOfWeek(String date) {
        return getDayOfWeek(parse(date));
    }

    /**
     * 区间日期
     *
     * @param fr fr
     * @param to to
     * @return List
     */
    public static List<Date> getDays(Date fr, Date to) {
        List<Date> list = new ArrayList<Date>();
        list.add(fr);
        while (true) {
            fr = addDay(fr, 1);
            if (diff(DATE_PART_DATE, fr, to) < 0) {
                break;
            }
            list.add(fr);
        }
        return list;
    }

    /**
     * 期间的月份
     *
     * @param fr yyyy-MM 或 yyyy-MM-dd
     * @param to yyyy-MM 或 yyyy-MM-dd
     * @return List
     */
    public static List<String> getMonths(String fr, String to) {
        List<String> list = new ArrayList<>();
        if (fr.length() < 10) {
            fr = fr + "-01";
        }
        if (to.length() < 10) {
            to = to + "-01";
        }
        Date ymd = parse(fr);
        Date last = parse(to);
        list.add(format(ymd, "yyyy-MM"));
        while (true) {
            ymd = addMonth(ymd, 1);
            if (diff(DATE_PART_MONTH, ymd, last) < 0) {
                break;
            }
            list.add(format(ymd, "yyyy-MM"));
        }
        return list;
    }

    public static List<Date> getDaysOfYear(int year) {
        return getDaysOfYear(parse(year + "-01-01"));
    }

    public static List<Date> getDaysOfYear(String year) {
        String ymd = year + "-01-01";
        if (year.length() > 4) {
            ymd = year;
        }
        return getDaysOfYear(parse(ymd));
    }

    public static List<Date> getDaysOfYear(Date date) {
        List<Date> list = new ArrayList<Date>();
        Date start = getFirstDayOfYear(date);
        int qty = countDaysOfYear(date);
        for (int i = 0; i < qty; i++) {
            list.add(addDay(start, i));
        }
        return list;
    }

    public static List<Date> getDaysOfMonth(String ym) {
        String ymd = ym + "-01";
        if (ym.length() > 7) {
            ymd = ym;
        }
        return getDaysOfMonth(parse(ymd));
    }

    public static List<Date> getDaysOfMonth(Date ym) {
        List<Date> list = new ArrayList<Date>();
        Date start = getFirstDayOfMonth(ym);
        int qty = countDaysOfMonth(ym);
        for (int i = 0; i < qty; i++) {
            Date date = addDay(start, i);
            list.add(date);
        }
        return list;
    }

    public static List<Date> getDaysOfWeek(int year, int week) {
        Calendar calendar = getCalendar();
        List<Date> list = new ArrayList<Date>();
        calendar.setTime(new Date());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        Date start = getFirstDayOfNextWeek(calendar.getTime());
        for (int i = 0; i < 7; i++) {
            Date date = addDay(start, i);
            list.add(date);
        }
        return list;
    }

    public static List<Date> getDaysOfWeek(Date date) {
        List<Date> list = new ArrayList<Date>();
        Date start = getFirstDayOfWeek(date);
        for (int i = 0; i < 7; i++) {
            list.add(addDay(start, i));
        }
        return list;
    }

    public static Date max(Date... dates) {
        Date result = null;
        if (null != dates) {
            for (Date date : dates) {
                if (null == result) {
                    result = date;
                } else {
                    if (null != date && diff(DATE_PART_SECOND, result, date) > 0) {
                        result = date;
                    }
                }
            }
        }
        return result;
    }

    public static Date min(Date... dates) {
        Date result = null;
        if (null != dates) {
            for (Date date : dates) {
                if (null == result) {
                    result = date;
                } else {
                    if (null != date && diff(DATE_PART_SECOND, result, date) < 0) {
                        result = date;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 一年中的第几个星期
     *
     * @param date date
     * @return int
     */
    public static int getWeekOfYear(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    /**
     * 按年加, 指定日期
     *
     * @param date  日期
     * @param value value
     * @return Date
     */
    public static Date addYear(Date date, int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, value);
        return calendar.getTime();
    }

    /**
     * 按小时加
     *
     * @param value value
     * @return Date
     */
    public static Date addHour(int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, value);
        return calendar.getTime();
    }

    /**
     * 按小时加, 指定日期
     *
     * @param date  日期
     * @param value value
     * @return Date
     */
    public static Date addHour(Date date, int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, value);
        return calendar.getTime();
    }

    /**
     * 按分钟加
     *
     * @param value value
     * @return Date
     */
    public static Date addMinute(int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, value);
        return calendar.getTime();
    }

    /**
     * 按分钟加, 指定日期
     *
     * @param date  日期
     * @param value value
     * @return Date
     */
    public static Date addMinute(Date date, int value) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, value);
        return calendar.getTime();
    }

    /**
     * 年份
     *
     * @param date date
     * @return int
     */
    public static int year(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int year() {
        return year(new Date());
    }

    /**
     * 月份
     *
     * @param date date
     * @return int
     */
    public static int month(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int month() {
        return month(new Date());
    }

    /**
     * 星期几(礼拜几)
     *
     * @param date date
     * @return int
     */
    public static int dayOfWeek(Date date) {
        return getDayOfWeek(date);
    }

    /**
     * 日(号)
     *
     * @param date date
     * @return int
     */
    public static int dayOfMonth(Date date) {
        return getDayOfMonth(date);
    }

    public static int dayOfMonth() {
        return getDayOfMonth();
    }

    public static int getDayOfMonth(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfMonth() {
        return getDayOfMonth(new Date());
    }


    public static int day(Date date) {
        return dayOfMonth(date);
    }

    public static int day() {
        return day(new Date());
    }

    public static int dayOfYear(Date date) {
        return getDayOfYear(date);
    }

    public static int dayOfYear() {
        return getDayOfYear();
    }

    public static int getDayOfYear(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int getDayOfYear() {
        return dayOfYear(new Date());
    }

    /**
     * 小时(点)
     *
     * @param date date
     * @return int
     */
    public static int hour(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 分钟
     *
     * @param date date
     * @return int
     */
    public static int minute(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 秒
     *
     * @param date date
     * @return int
     */
    public static int second(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * date1 大于  date2返回 1
     *
     * @param date1 date2
     * @param date2 date2
     * @return int
     */
    public static int compare(Date date1, Date date2) {
        long _date1 = date1.getTime();
        long _date2 = date2.getTime();
        if (_date1 > _date2) {
            return 1;
        } else if (_date1 == _date2) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 是上午吗?
     *
     * @param date date
     * @return boolean
     */
    public static boolean isAm(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.AM_PM) == 0;
    }

    /**
     * 是下午吗?
     *
     * @param date date
     * @return boolean
     */
    public static boolean isPm(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.AM_PM) == 1;
    }

    /**
     * 毫秒转换成*天*时*分*秒*毫秒
     *
     * @param src 毫秒
     * @return String
     */
    public static String conversion(long src) {
        String result = "";
        long s = 0;
        long m = 0;
        long h = 0;
        long d = 0;
        long ms = 0;
        d = src / 1000 / 60 / 60 / 24;
        h = (src - d * 24 * 60 * 60 * 1000) / 1000 / 60 / 60;
        m = (src - d * 24 * 60 * 60 * 1000 - h * 60 * 60 * 1000) / 1000 / 60;
        s = (src - d * 24 * 60 * 60 * 1000 - h * 60 * 60 * 1000 - m * 60 * 1000) / 1000;
        ms = src % 1000;
        if (d > 0) {
            result += d + "天";
        }
        if (h > 0
                || (d > 0 && (m + s + ms > 0))
        ) {
            result += h + "时";
        }
        if (m > 0
                || (h > 0 && (s + ms > 0))
                || (d > 0 && (s + ms > 0))
        ) {
            result += m + "分";
        }
        if (s > 0
                || (m > 0 && ms > 0)
                || (h > 0 && ms > 0)
                || (d > 0 && ms > 0)
        ) {
            if (ms == 0) {
                result += s + "秒";
            } else {
                result += s + "." + BasicUtil.fillChar(ms + "", 3) + "秒";
            }
        }
        if (src < 1000) {
            if (ms > 0) {
                result += ms + "毫秒";
            }
        }
        if (src <= 0) {
            result = "0毫秒";
        }
        return result;
    }

    public static LocalDateTime localDateTime(Date date, ZoneId zone) {
        if (null == date) {
            return null;
        }
        return date.toInstant().atZone(zone).toLocalDateTime();
    }

    public static OffsetDateTime offsetDateTime(Date date) {
        return OffsetDateTime.of(localDateTime(date), ZoneOffset.UTC);
    }

    public static LocalDateTime localDateTime(Date date) {
        return localDateTime(date, ZoneId.systemDefault());
    }

    public static LocalTime localTime(Date date, ZoneId zone) {
        if (null == date) {
            return null;
        }
        return date.toInstant().atZone(zone).toLocalTime();
    }

    public static LocalTime localTime(Date date) {
        return localTime(date, ZoneId.systemDefault());
    }

    public static LocalDate localDate(Date date, ZoneId zone) {
        if (null == date) {
            return null;
        }
        return date.toInstant().atZone(zone).toLocalDate();
    }

    public static LocalDate localDate(Date date) {
        return localDate(date, ZoneId.systemDefault());
    }

    public static LocalDateTime localDateTime(Long timestamp, ZoneId zone) {
        return localDateTime(parse(timestamp), zone);
    }

    public static LocalDateTime localDateTime(Long timestamp) {
        return localDateTime(parse(timestamp), ZoneId.systemDefault());
    }

    public static LocalTime localTime(Long timestamp, ZoneId zone) {
        return localTime(parse(timestamp), zone);
    }

    public static LocalTime localTime(Long timestamp) {
        return localTime(timestamp, ZoneId.systemDefault());
    }

    public static LocalDate localDate(Long timestamp, ZoneId zone) {
        return localDate(parse(timestamp), zone);
    }

    public static LocalDate localDate(Long timestamp) {
        return localDate(timestamp, ZoneId.systemDefault());
    }


    public static java.sql.Date sqlDate(Date date) {
        java.sql.Date result = null;
        if (null != date) {
            result = new java.sql.Date(date.getTime());
        }
        return result;
    }

    public static java.sql.Date sqlDate(Timestamp date) {
        java.sql.Date result = null;
        if (null != date) {
            result = new java.sql.Date(date.getTime());
        }
        return result;
    }

    public static java.sql.Time sqlTime(Date date) {
        java.sql.Time result = null;
        if (null != date) {
            result = java.sql.Time.valueOf(format(date, "HH:mm:ss"));
        }
        return result;
    }

    public static YearMonth yearMonth(Date date) {
        return YearMonth.of(year(date), month(date));
    }
}
