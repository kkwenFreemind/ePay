package com.apt.util;


import com.apt.epay.share.ShareParm;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
//import java.util.Locale;
//import java.util.TimeZone;

public class DateUtil {

    /**
     * 時間範圍：年
     */
    public static final int YEAR = 1;
    /**
     * 時間範圍：季度
     */
    public static final int QUARTER = 2;
    /**
     * 時間範圍：月
     */
    public static final int MONTH = 3;
    /**
     * 時間範圍：旬
     */
    public static final int TENDAYS = 4;
    /**
     * 時間範圍：周
     */
    public static final int WEEK = 5;
    /**
     * 時間範圍：日
     */
    public static final int DAY = 6;

    /* 基準時間 */
    private Date fiducialDate = null;
    private Calendar cal = null;

    public DateUtil() {
    }

    public DateUtil(Date fiducialDate) {
        if (fiducialDate != null) {
            this.fiducialDate = fiducialDate;
        } else {
            this.fiducialDate = new Date(System.currentTimeMillis());
        }

        this.cal = Calendar.getInstance();
        this.cal.setTime(this.fiducialDate);
        this.cal.set(Calendar.HOUR_OF_DAY, 0);
        this.cal.set(Calendar.MINUTE, 0);
        this.cal.set(Calendar.SECOND, 0);
        this.cal.set(Calendar.MILLISECOND, 0);

        this.fiducialDate = this.cal.getTime();
    }

    /**
     * 將日期轉化成 oracle 的 to_date('xxx','xxx') 格式
     *
     * @param d 日期
     * @param format 日期格式，例如 "yyyy-MM-dd HH:mm"
     * @param hqlFormat oracle的日期格式，例如："yyyy-mm-dd hh24:mi"
     * @return
     */
    public static String toOracleDate(Date d, String format, String hqlFormat) {
        StringBuffer bf = new StringBuffer();
        bf.append("to_date('");
        bf.append(dateFormat(d, format));
        bf.append("','");
        bf.append(hqlFormat);
        bf.append("')");
        return bf.toString();
    }

    /**
     * 將日期轉化成指定格式的字符串
     *
     * @param d 日期
     * @param formatStr 字符串格式
     * @return
     */
    private static String dateFormat(Date d, String formatStr) {
        return (new java.text.SimpleDateFormat(formatStr).format(d));
    }

    /**
     * 獲取DateHelper實例
     * @param fiducialDate
     * 基準時間
     * @return
     */
    public static DateUtil getInstance(Date fiducialDate) {
        return new DateUtil(fiducialDate);
    }

    /**
     * 獲取DateHelper實例, 使用當前時間作為基準時間
     * @return
     */
    public static DateUtil getInstance() {
        return new DateUtil(null);
    }

    /**
     * 獲取年的第一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getFirstDayOfYear(int offset) {
        cal.setTime(this.fiducialDate);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + offset);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 獲取年的最後一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getLastDayOfYear(int offset) {
        cal.setTime(this.fiducialDate);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + offset);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        return cal.getTime();
    }

    /**
     * 獲取季度的第一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getFirstDayOfQuarter(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.MONTH, offset * 3);
        int mon = cal.get(Calendar.MONTH);
        if (mon >= Calendar.JANUARY && mon <= Calendar.MARCH) {
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (mon >= Calendar.APRIL && mon <= Calendar.JUNE) {
            cal.set(Calendar.MONTH, Calendar.APRIL);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (mon >= Calendar.JULY && mon <= Calendar.SEPTEMBER) {
            cal.set(Calendar.MONTH, Calendar.JULY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (mon >= Calendar.OCTOBER && mon <= Calendar.DECEMBER) {
            cal.set(Calendar.MONTH, Calendar.OCTOBER);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        return cal.getTime();
    }

    /**
     * 獲取一天的最後時間
     * @param date
     * Date
     * @return Date
     */
    public static Date getLastTimeOfDay(Date date) {
        Date rtresult = null;
        SimpleDateFormat sdf_lasttime = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT_LASTTIME);
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        try {
            rtresult = sdf.parse(sdf_lasttime.format(date));
        } catch (Exception ex) {
        }
        return rtresult;
    }

    /**
     * 獲取季度的最後一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getLastDayOfQuarter(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.MONTH, offset * 3);

        int mon = cal.get(Calendar.MONTH);
        if (mon >= Calendar.JANUARY && mon <= Calendar.MARCH) {
            cal.set(Calendar.MONTH, Calendar.MARCH);
            cal.set(Calendar.DAY_OF_MONTH, 31);
        }
        if (mon >= Calendar.APRIL && mon <= Calendar.JUNE) {
            cal.set(Calendar.MONTH, Calendar.JUNE);
            cal.set(Calendar.DAY_OF_MONTH, 30);
        }
        if (mon >= Calendar.JULY && mon <= Calendar.SEPTEMBER) {
            cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 30);
        }
        if (mon >= Calendar.OCTOBER && mon <= Calendar.DECEMBER) {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 31);
        }
        return cal.getTime();
    }

    /**
     * 獲取月的偏移量日期
     * @param offset
     * 偏移量
     * @return
     */
    public Date getDayOfMonth(int offset) {
        cal.setTime(this.fiducialDate);
        // cal.add(Calendar.MONTH, offset);
        cal.set(Calendar.DAY_OF_MONTH, offset + 1);
        return cal.getTime();
    }

    /**
     * 獲取月的最後一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getFirstDayOfMonth(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.MONTH, offset);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 獲取月的最後一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getLastDayOfMonth(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.MONTH, offset + 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 獲取旬的第一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getFirstDayOfTendays(int offset) {
        cal.setTime(this.fiducialDate);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day >= 21) {
            day = 21;
        } else if (day >= 11) {
            day = 11;
        } else {
            day = 1;
        }

        if (offset > 0) {
            day = day + 10 * offset;
            int monOffset = day / 30;
            day = day % 30;
            cal.add(Calendar.MONTH, monOffset);
            cal.set(Calendar.DAY_OF_MONTH, day);
        } else {
            int monOffset = 10 * offset / 30;
            int dayOffset = 10 * offset % 30;
            if ((day + dayOffset) > 0) {
                day = day + dayOffset;
            } else {
                monOffset = monOffset - 1;
                day = day - dayOffset - 10;
            }
            cal.add(Calendar.MONTH, monOffset);
            cal.set(Calendar.DAY_OF_MONTH, day);
        }
        return cal.getTime();
    }

    /**
     * 獲取旬的最後一天
     * @param offset
     * 偏移量
     * @return
     */
    public Date getLastDayOfTendays(int offset) {
        Date date = getFirstDayOfTendays(offset + 1);
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 獲取周的第一天(MONDAY)
     * @param offset
     * 偏移量
     * @return
     */
    public Date getFirstDayOfWeek(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.DAY_OF_MONTH, offset * 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 獲取周的第二天(TUESDAY)
     * @param offset
     * 偏移量
     * @return
     */
    public Date getTuesdayDayOfWeek(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.DAY_OF_MONTH, offset * 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        return cal.getTime();
    }

    /**
     * 獲取周的最後一天(SUNDAY)
     * @param offset
     * 偏移量
     * @return
     */
    public Date getLastDayOfWeek(int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(Calendar.DAY_OF_MONTH, offset * 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, 6);
        return cal.getTime();
    }

    /**
     * 獲取指定時間範圍的第一天
     * @param dateRangeType
     * 時間範圍類型
     * @param offset
     * 偏移量
     * @return
     */
    private Date getFirstDate(int dateRangeType, int offset) {
        return null;
    }

    /**
     * 獲取指定時間範圍的最後一天
     * @param dateRangeType
     * 時間範圍類型
     * @param offset
     * 偏移量
     * @return
     */
    private Date getLastDate(int dateRangeType, int offset) {
        return null;
    }

    /**
     * 根據日曆的規則，為基準時間添加指定日曆欄位的時間量
     * @param field
     * 日曆欄位, 使用Calendar類定義的日曆欄位常量
     * @param offset
     * 偏移量
     * @return
     */
    public Date add(int field, int offset) {
        cal.setTime(this.fiducialDate);
        cal.add(field, offset);
        return cal.getTime();
    }

    /**
     * 根據日曆的規則，為基準時間添加指定日曆欄位的單個時間單元
     * @param field
     * 日曆欄位, 使用Calendar類定義的日曆欄位常量
     * @param up
     * 指定日曆欄位的值的滾動方向。true:向上滾動 / false:向下滾動
     * @return
     */
    public Date roll(int field, boolean up) {
        cal.setTime(this.fiducialDate);
        cal.roll(field, up);
        return cal.getTime();
    }

    /**
     * 把字串轉換為日期
     * @param dateStr 
     * 日期字串
     * @param format
     * 日期格式
     * @return 
     */
    public static Date strToDate(String dateStr, String format) {
        Date date = null;

        if (dateStr != null && (!dateStr.equals(""))) {
            DateFormat df = new SimpleDateFormat(format);
            try {
                date = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 把字串轉換為日期，日期的格式為yyyy-MM-dd HH:ss
     * @param dateStr 
     * 日期字串
     * @return 
     */
    public static Date strToDate(String dateStr) {
        Date date = null;

        if (dateStr != null && (!dateStr.equals(""))) {
            if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                dateStr = dateStr + " 00:00";
            } else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}")) {
                dateStr = dateStr + ":00";
            } else {
                System.out.println(dateStr + " 格式不正確");
                return null;
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:ss");
            try {
                date = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 把日期轉換為字串
     * @param date
     * 日期實例
     * @param format
     * 日期格式
     * @return
     */
    public static String dateToStr(Date date, String format) {
        return (date == null) ? "" : new SimpleDateFormat(format).format(date);
    }

    /**
     * 取得當前日期 年-月-日
     * @return
     */
    public static String getCurrentDateStr() {
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(Calendar.getInstance().getTime());
    }

    /** 
     * getDateStr get a string with format YYYY-MM-DD from a Date object 
     * 
     * @param date 
     * date 
     * @return String 
     */
    static public String getDateStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 把日期轉換為字串-返回年格式yyyy
     * @param date
     * 日期實例
     * @param format
     * 日期格式
     * @return
     */
    static public String getYear(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(date);
    }

    /**
     * 把日期轉換為字串-返回月格式MM
     * @param date
     * 日期實例
     * @param format
     * 日期格式
     * @return
     */
    static public String getMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return format.format(date);
    }

    /**
     * 把日期轉換為字串-返回中文格式單位yyyy年MM月dd日
     * @param date
     * 日期實例
     * @param format
     * 日期格式
     * @return
     */
    static public String getDateStrC(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    /**
     * 把日期轉換為字串-返回連續字串yyyyMMdd
     * @param date
     * 日期實例
     * @param format
     * 日期格式
     * @return
     */
    static public String getDateStrCompact(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String str = format.format(date);
        return str;
    }

    /** 
     * getDateStr get a string with format YYYY-MM-DD HH:mm:ss from a Date 
     * object 
     * 
     * @param date 
     * date 
     * @return String 
     */
    static public String getDateTimeStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 把日期轉換為字串-返回中格式日期時間yyyy年MM月dd日 HH時mm分ss秒
     * @param date
     * 日期實例
     * @param format
     * 日期格式
     * @return
     */
    static public String getDateTimeStrC(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");
        return format.format(date);
    }

    /**
     * 依照自訂日期格式輸出
     * @param pattern
     * 自訂日期格式
     * @param format
     * 日期格式
     * @return
     */
    public static String getCurDateStr(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    /** 
     * Parses text in 'YYYY-MM-DD' format to produce a date. 
     * 
     * @param s 
     * the text 
     * @return Date 
     * @throws ParseException 
     */
    static public Date parseDate(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(s);
    }

    /**
     * 解析中文日期字串格式
     * @param s
     * 中文日期字串
     * @return Date
     */
    static public Date parseDateC(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.parse(s);
    }

    /** 
     * Parses text in 'YYYY-MM-DD' format to produce a date. 
     * 
     * @param s 
     * the text 
     * @return Date 
     * @throws ParseException 
     */
    static public Date parseDateTime(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(s);
    }

    static public Date parseDateTimeC(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");
        return format.parse(s);
    }

    /** 
     * Parses text in 'HH:mm:ss' format to produce a time. 
     * 
     * @param s 
     * the text 
     * @return Date 
     * @throws ParseException 
     */
    static public Date parseTime(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.parse(s);
    }

    static public Date parseTimeC(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH時mm分ss秒");
        return format.parse(s);
    }

    static public int yearOfDate(Date s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String d = format.format(s);
        return Integer.parseInt(d.substring(0, 4));
    }

    static public int monthOfDate(Date s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String d = format.format(s);
        return Integer.parseInt(d.substring(5, 7));
    }

    static public int dayOfDate(Date s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String d = format.format(s);
        return Integer.parseInt(d.substring(8, 10));
    }

    static public String getDateTimeStr(java.sql.Date date, double time) {
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        String dateStr = year + "-" + month + "-" + day;
        Double d = new Double(time);
        String timeStr = String.valueOf(d.intValue()) + ":00:00";

        return dateStr + " " + timeStr;
    }

    /** 
     * Get the total month from two date. 
     * 
     * @param sd 
     * the start date 
     * @param ed 
     * the end date 
     * @return int month form the start to end date 
     * @throws ParseException 
     */
    static public int diffDateM(Date sd, Date ed) throws ParseException {
        return (ed.getYear() - sd.getYear()) * 12 + ed.getMonth() - sd.getMonth() + 1;
    }

    static public int diffDateD(Date sd, Date ed) throws ParseException {
        return Math.round((ed.getTime() - sd.getTime()) / 86400000) + 1;
    }

    static public int diffDateM(int sym, int eym) throws ParseException {
        return (Math.round(eym / 100) - Math.round(sym / 100)) * 12 + (eym % 100 - sym % 100) + 1;
    }

    static public java.sql.Date getNextMonthFirstDate(java.sql.Date date)
            throws ParseException {
        Calendar scalendar = new GregorianCalendar();
        scalendar.setTime(date);
        scalendar.add(Calendar.MONTH, 1);
        scalendar.set(Calendar.DATE, 1);
        return new java.sql.Date(scalendar.getTime().getTime());
    }

    static public java.sql.Date getFrontDateByDayCount(java.sql.Date date,
            int dayCount) throws ParseException {
        Calendar scalendar = new GregorianCalendar();
        scalendar.setTime(date);
        scalendar.add(Calendar.DATE, -dayCount);
        return new java.sql.Date(scalendar.getTime().getTime());
    }

    /** 
     * Get first day of the month. 
     * 
     * @param year 
     * the year 
     * @param month 
     * the month 
     * @return Date first day of the month. 
     * @throws ParseException 
     */
    static public Date getFirstDay(String year, String month)
            throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(year + "-" + month + "-1");
    }

    static public Date getFirstDay(int year, int month) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(year + "-" + month + "-1");
    }

    static public Date getLastDay(String year, String month)
            throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(year + "-" + month + "-1");

        Calendar scalendar = new GregorianCalendar();
        scalendar.setTime(date);
        scalendar.add(Calendar.MONTH, 1);
        scalendar.add(Calendar.DATE, -1);
        date = scalendar.getTime();
        return date;
    }

    static public Date getLastDay(int year, int month) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(year + "-" + month + "-1");

        Calendar scalendar = new GregorianCalendar();
        scalendar.setTime(date);
        scalendar.add(Calendar.MONTH, 1);
        scalendar.add(Calendar.DATE, -1);
        date = scalendar.getTime();
        return date;
    }

    /** 
     * getToday get todat string with format YYYY-MM-DD from a Date object 
     * 
     * @param date 
     * date 
     * @return String 
     */
    static public String getTodayStr() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        return getDateStr(calendar.getTime());
    }

    static public Date getToday() throws ParseException {
        return new Date(System.currentTimeMillis());
    }

    static public String getTodayAndTime() {
        return new Timestamp(System.currentTimeMillis()).toString();
    }

    static public String getTodayC() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        return getDateStrC(calendar.getTime());
    }

    static public int getThisYearMonth() throws ParseException {
        Date today = Calendar.getInstance().getTime();
        return (today.getYear() + 1900) * 100 + today.getMonth() + 1;
    }

    static public int getYearMonth(Date date) throws ParseException {
        return (date.getYear() + 1900) * 100 + date.getMonth() + 1;
    }

// 獲取相隔月數 
    static public long getDistinceMonth(String beforedate, String afterdate)
            throws ParseException {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        long monthCount = 0;
        try {
            java.util.Date d1 = d.parse(beforedate);
            java.util.Date d2 = d.parse(afterdate);

            monthCount = (d2.getYear() - d1.getYear()) * 12 + d2.getMonth() - d1.getMonth();
// dayCount = (d2.getTime()-d1.getTime())/(30*24*60*60*1000); 

        } catch (ParseException e) {
            System.out.println("Date parse error!");
// throw e; 
        }
        return monthCount;
    }

// 獲取相隔天數 
    static public long getDistinceDay(String beforedate, String afterdate)
            throws ParseException {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        long dayCount = 0;
        try {
            java.util.Date d1 = d.parse(beforedate);
            java.util.Date d2 = d.parse(afterdate);

            dayCount = (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            System.out.println("Date parse error!");
// throw e; 
        }
        return dayCount;
    }

// 獲取相隔天數 
    static public long getDistinceDay(Date beforedate, Date afterdate)
            throws ParseException {
        long dayCount = 0;

        try {
            dayCount = (afterdate.getTime() - beforedate.getTime()) / (24 * 60 * 60 * 1000);

        } catch (Exception e) {
// System.out.println("Date parse error!"); 
// // throw e; 
        }
        return dayCount;
    }

    static public long getDistinceDay(java.sql.Date beforedate,
            java.sql.Date afterdate) throws ParseException {
        long dayCount = 0;

        try {
            dayCount = (afterdate.getTime() - beforedate.getTime()) / (24 * 60 * 60 * 1000);

        } catch (Exception e) {
// System.out.println("Date parse error!"); 
// // throw e; 
        }
        return dayCount;
    }

// 獲取相隔天數 
    static public long getDistinceDay(String beforedate) throws ParseException {
        return getDistinceDay(beforedate, getTodayStr());
    }

// 獲取相隔時間數 
    static public long getDistinceTime(String beforeDateTime,
            String afterDateTime) throws ParseException {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long timeCount = 0;
        try {
            java.util.Date d1 = d.parse(beforeDateTime);
            java.util.Date d2 = d.parse(afterDateTime);

            timeCount = (d2.getTime() - d1.getTime()) / (60 * 60 * 1000);

        } catch (ParseException e) {
            System.out.println("Date parse error!");
            throw e;
        }
        return timeCount;
    }

// 獲取相隔時間數 
    static public long getDistinceTime(String beforeDateTime)
            throws ParseException {
        return getDistinceTime(beforeDateTime, new Timestamp(System.currentTimeMillis()).toLocaleString());
    }

// 獲取相隔分鐘數 
    static public long getDistinceMinute(String beforeDateTime,
            String afterDateTime) throws ParseException {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeCount = 0;
        try {
            java.util.Date d1 = d.parse(beforeDateTime);
            java.util.Date d2 = d.parse(afterDateTime);
            timeCount = (d2.getTime() - d1.getTime()) / (60 * 1000);

        } catch (ParseException e) {
            System.out.println("Date parse error!");
            throw e;
        }
        return timeCount;
    }

    /**
     * 獲得當前年份
     * 
     * @return 當前年份，格式如：2003
     */
    public static int getCurrentYear() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy");
        return Integer.parseInt(sdf.format(new java.util.Date()));
    }

    /**
     * 獲得當前月份
     * 
     * @return 當前月份
     */
    public static int getCurrentMonth() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("M");
        return Integer.parseInt(sdf.format(new java.util.Date()));
    }

    /**
     * 獲得當前天
     * 
     * @return 當前天
     */
    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }

    public static String getCurrentDateTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd H:mm");
        return sdf.format(new Date());
    }

    /**
     * 獲得形如 19770608 格式的當前年月日
     * 
     * @return 當前年月日
     */
    public static String getSimpleCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyyMMdd HH:mm:ss");
        return sdf.format(new java.util.Date());
    }

    /**
     * 返回兩個日期相差天數
     * 
     * @param d1
     *            日期
     * @param d2
     *            日期
     * @return 天數
     */
    public int diffDate(Date d1, Date d2) {
        if ((d1 == null) || (d2 == null)) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        // from Locale, has nothing to do with your input date format
        int zoneoffset = cal.get(Calendar.ZONE_OFFSET);
        int dstoffset = cal.get(Calendar.DST_OFFSET);
        // getTime() return absolute GMT time
        // compensate with the offsets
        long dl1 = d1.getTime() + zoneoffset + dstoffset;
        long dl2 = d2.getTime() + zoneoffset + dstoffset;
        int intDaysFirst = (int) (dl1 / (60 * 60 * 1000 * 24)); //60*60*1000

        int intDaysSecond = (int) (dl2 / (60 * 60 * 1000 * 24));
        return intDaysFirst > intDaysSecond ? intDaysFirst - intDaysSecond
                : intDaysSecond - intDaysFirst;
    }

    /**
     * 將給定的時間轉換為格式是8位元的字串
     * 
     * @param date
     *            給定的時間
     * @return 格式化後的字串形式的時間
     */
    public String get8BitDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyyMMdd");
        return sdf.format(date);
    }

    public String to_date(String strdate, String df) {
        if (strdate == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat(
                "M/d/yyyy H:m:s");
        Date d = null;
        try {
            d = sdf1.parse(strdate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return sdf.format(d);
    }

    public static String get8BitString(String strDate) {
        if (strDate == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(
                "yyyyMMdd");
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return sdf2.format(d);
    }

    public static String get8ByteTo10Byte(String strDate) {
        if (strDate == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyyMMdd");
        java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return sdf2.format(d);
    }

    public static String getStandedDateTime(String strDate) {
        if (strDate == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return sdf2.format(d);
    }

    public static String getMonthDay(java.util.Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("M月d日");
        return sdf.format(date);
    }

    public static String getHourMinute(java.util.Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("H:mm");
        return sdf.format(date);
    }

    /**
     * 判斷字串是否符合日期格式
     * 
     * @param str
     *            字串時間
     * @return
     */
    public static boolean isDate(String strDate) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(strDate);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    /**
     * 判斷是否是數字
     * 
     * @param str
     * @return
     */
    public static boolean isNumber(String strNumber) {
        boolean bolResult = false;
        try {
            Double.parseDouble(strNumber);
            bolResult = true;
        } catch (NumberFormatException ex) {
            bolResult = false;
        }
        return bolResult;
    }

    public String dateadd(Date strDate, int a) {
        String str = "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd");
        String strDate1 = sdf.format(strDate);
        int year = Integer.parseInt(strDate1.substring(0, 4));
        int month = Integer.parseInt(strDate1.substring(5, 7));
        int day = Integer.parseInt(strDate1.substring(8, 10));
        int md = getdayformonth(month);
        int i = (day + a) / md;
        int j = (day + a) % md;
        if (j == 0) {
            i = i - 1;
            j = md;
        }
        String strmon = "";
        String strday = "";
        String mondiff = "";
        if (i < 2) {
            if (Integer.toString(j).length() == 1) {
                strday = "0" + Integer.toString(j);
            } else {
                strday = Integer.toString(j);
            }
            if ((month + i) > 12) {
                int yeardiff = (month + i) / 12;
                int monthmod = (month + i) % 12;
                mondiff = Integer.toString(monthmod);
                if (Integer.toString(monthmod).length() == 1) {
                    mondiff = "0" + Integer.toString(monthmod);
                }
                str = Integer.toString(year + yeardiff) + "-" + mondiff + "-" + strday;
            } else {
                strmon = Integer.toString(month + i);
                if (Integer.toString(month + i).length() == 1) {
                    strmon = "0" + Integer.toString(month + i);
                }
                str = Integer.toString(year) + "-" + strmon + "-" + strday;
            }
        } else {
            //主要判斷假如天數，月份溢出的處理，
        }
        return str;
    }

    public int getdayformonth(int month) {
        int a = 0;
        switch (month) {
            case 1:
                a = 31;
                break;
            case 2:
                a = 28;
                break;
            case 3:
                a = 31;
                break;
            case 4:
                a = 30;
                break;
            case 5:
                a = 31;
                break;
            case 6:
                a = 30;
                break;
            case 7:
                a = 31;
                break;
            case 8:
                a = 31;
                break;
            case 9:
                a = 30;
                break;
            case 10:
                a = 31;
                break;
            case 11:
                a = 30;
                break;
            case 12:
                a = 31;
                break;
            default:
        }
        return a;
    }

    public String addOneDay(String strDate) //YYYY-MM-DD
    {
        int[] standardDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] leapyearDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int y = Integer.parseInt(strDate.substring(0, 4));
        int m = Integer.parseInt(strDate.substring(4, 6));
        int d = Integer.parseInt(strDate.substring(6, 8)) + 1;
        int maxDateCount = 0;
        System.out.println(y);
        System.out.println(m);
        System.out.println(d);
        if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
            maxDateCount = leapyearDays[m - 1];
        } else {
            maxDateCount = standardDays[m - 1];
        }
        if (d > maxDateCount) {
            d = 1;
            m++;
        }
        if (m > 12) {
            m = 1;
            y++;
        }
        java.text.DecimalFormat yf = new java.text.DecimalFormat("0000");
        java.text.DecimalFormat mdf = new java.text.DecimalFormat("00");
        return yf.format(y) + mdf.format(m) + mdf.format(d);
    }

    public static String subOneDay(String strDate) {
        //YYYY-MM-DD
        int[] standardDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int[] leapyearDays = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int y = Integer.parseInt(strDate.substring(0, 4));
        int m = Integer.parseInt(strDate.substring(4, 6));
        int d = Integer.parseInt(strDate.substring(6, 8)) - 1;
        int maxDateCount = 0;
        System.out.println(y);
        System.out.println(m);
        System.out.println(d);
        if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
            maxDateCount = leapyearDays[m - 1];
        } else {
            maxDateCount = standardDays[m - 1];
        }
        if (d > maxDateCount) {
            d = 1;
            m++;
        }
        if (m > 12) {
            m = 1;
            y++;
        }
        java.text.DecimalFormat yf = new java.text.DecimalFormat("0000");
        java.text.DecimalFormat mdf = new java.text.DecimalFormat("00");
        return yf.format(y) + mdf.format(m) + mdf.format(d);
    }

    public static String getChineseDate(Date d) {
        if (d == null) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", new DateFormatSymbols());
        String dtrDate = df.format(d);
        return dtrDate.substring(0, 4) + "\u5E74" + Integer.parseInt(dtrDate.substring(4, 6)) + "\u6708" + Integer.parseInt(dtrDate.substring(6, 8)) + "\u65E5";
    }

    /**
     * 當前日期
     * @return
     */
    public static String getCurrentDate_String() {
        Calendar cal = Calendar.getInstance();
        String currentDate = null;
        String currentYear = (new Integer(cal.get(1))).toString();
        String currentMonth = null;
        String currentDay = null;
        if (cal.get(2) < 9) {
            currentMonth = "0" + (new Integer(cal.get(2) + 1)).toString();
        } else {
            currentMonth = (new Integer(cal.get(2) + 1)).toString();
        }
        if (cal.get(5) < 10) {
            currentDay = "0" + (new Integer(cal.get(5))).toString();
        } else {
            currentDay = (new Integer(cal.get(5))).toString();
        }
        currentDate =
                currentYear + currentMonth + currentDay;
        return currentDate;
    }

    /**
     * 取當前日期
     * @param strFormat 日期格式
     * @return
     */
    public static String getCurrentDate_String(String strFormat) {
        Calendar cal = Calendar.getInstance();
        Date d = cal.getTime();
        return getDate(d, strFormat);
    }

    /**
     * 返回兩個年月之間間隔的月數
     * @param dealMonth
     * @param alterMonth
     * @return
     * @pre alterMonth != null
     * @pre dealMonth != null
     */
    public static int calBetweenTwoMonth(String dealMonth, String alterMonth) {
        int length = 0;
        if (dealMonth.length() != 6 || alterMonth.length() != 6) {
            // Helper.println("\u6BD4\u8F83\u5E74\u6708\u5B57\u7B26\u4E32\u7684\u957F\u5EA6\u4E0D\u6B63\u786E");
            length = -1;
        } else {
            int dealInt = Integer.parseInt(dealMonth);
            int alterInt = Integer.parseInt(alterMonth);
            if (dealInt < alterInt) {
                //  Helper.println("\u7B2C\u4E00\u4E2A\u5E74\u6708\u53D8\u91CF\u5E94\u5927\u4E8E\u6216\u7B49\u4E8E\u7B2C\u4E8C\u4E2A\u5E74\u6708\u53D8\u91CF");
                length = -2;
            } else {
                int dealYearInt = Integer.parseInt(dealMonth.substring(0, 4));
                int dealMonthInt = Integer.parseInt(dealMonth.substring(4, 6));
                int alterYearInt = Integer.parseInt(alterMonth.substring(0, 4));
                int alterMonthInt = Integer.parseInt(alterMonth.substring(4, 6));
                length =
                        (dealYearInt - alterYearInt) * 12 + (dealMonthInt - alterMonthInt);
            }

        }
        return length;
    }

    /**
     * 返回年
     * @param date
     * @return
     * @pre date != null
     */
    public static int convertDateToYear(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy",
                new DateFormatSymbols());
        return Integer.parseInt(df.format(date));
    }

    /**
     * 返回年月
     * @param d
     * @return
     * @pre d != null
     */
    public static String convertDateToYearMonth(
            Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMM",
                new DateFormatSymbols());
        return df.format(d);
    }

    /**
     * 返回年月日
     * @param d
     * @return
     * @pre d != null
     */
    public static String convertDateToYearMonthDay(
            Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd",
                new DateFormatSymbols());
        return df.format(d);
    }

    /**
     * 計算日期間隔的天數
     * @param beginDate 開始日期
     * @param endDate 結束日期
     * @return
     * @pre beginDate != null
     */
    public static int daysBetweenDates(Date beginDate, Date endDate) {
        int days = 0;
        Calendar calo = Calendar.getInstance();
        Calendar caln = Calendar.getInstance();
        calo.setTime(beginDate);
        caln.setTime(endDate);
        int oday = calo.get(6);
        int nyear = caln.get(1);
        for (int oyear = calo.get(1); nyear > oyear;) {
            calo.set(2, 11);
            calo.set(5, 31);
            days += calo.get(6);
            oyear++;

            calo.set(1, oyear);
        }

        int nday = caln.get(6);
        days = (days + nday) - oday;
        return days;
    }

    /**
     * 計算間隔天數後的日期
     * @param date
     * @param intBetween
     * @return
     * @pre date != null
     */
    public static Date getDateBetween(
            Date date, int intBetween) {
        Calendar calo = Calendar.getInstance();
        calo.setTime(date);
        calo.add(Calendar.DATE, intBetween);
        return calo.getTime();
    }

    /**
     * 計算指定天數後的日期
     * @param date 
     * @param intBetween
     * @param strFromat 日期格式
     * @return
     */
    public static String getDateBetween_String(
            Date date, int intBetween,
            String strFromat) {
        Date dateOld = getDateBetween(date, intBetween);
        return getDate(dateOld, strFromat);
    }

    /**
     * 年月值增1
     * @param yearMonth
     * @return
     * @pre yearMonth != null
     * @throws java.lang.StringIndexOutOfBoundsException
     */
    public static String increaseYearMonth(
            String yearMonth) {
        int year = (new Integer(yearMonth.substring(0, 4))).intValue();
        int month = (new Integer(yearMonth.substring(4, 6))).intValue();
        if (++month <= 12 && month >= 10) {
            return yearMonth.substring(0, 4) + (new Integer(month)).toString();
        }
        if (month < 10) {
            return yearMonth.substring(0, 4) + "0" + (new Integer(month)).toString();
        }
        return (new Integer(year + 1)).toString() + "0" + (new Integer(month - 12)).toString();
    }

    /**
     * 年月值增加指定的值
     * @param yearMonth 初始年月
     * @param addMonth 指定的值
     * @return
     * @pre yearMonth != null
     * @throws java.lang.StringIndexOutOfBoundsException
     */
    public static String increaseYearMonth(
            String yearMonth, int addMonth) {
        int year = (new Integer(yearMonth.substring(0, 4))).intValue();
        int month = (new Integer(yearMonth.substring(4, 6))).intValue();
        month += addMonth;
        year += month / 12;
        month %= 12;
        if (month <= 12 && month >= 10) {
            return year + (new Integer(month)).toString();
        }
        return year + "0" + (new Integer(month)).toString();
    }

    /**
     * 年月值減1
     * @param yearMonth
     * @return
     * @pre yearMonth != null
     * @throws java.lang.StringIndexOutOfBoundsException
     */
    public static String descreaseYearMonth(
            String yearMonth) {
        int year = (new Integer(yearMonth.substring(0, 4))).intValue();
        int month = (new Integer(yearMonth.substring(4, 6))).intValue();
        if (--month >= 10) {
            return yearMonth.substring(0, 4) + (new Integer(month)).toString();
        }
        if (month > 0 && month < 10) {
            return yearMonth.substring(0, 4) + "0" + (new Integer(month)).toString();
        }
        return (new Integer(year - 1)).toString() + (new Integer(month + 12)).toString();
    }

    /**
     * 年月值增1
     * @param yearMonth
     * @return
     */
    public static String addYearMonth(
            String yearMonth) {
        int year = (new Integer(yearMonth.substring(0, 4))).intValue();
        int month = (new Integer(yearMonth.substring(4, 6))).intValue();
        if (++month >= 10 && month < 12) {
            return yearMonth.substring(0, 4) + (new Integer(month)).toString();
        }
        if (month > 0 && month < 10) {
            return yearMonth.substring(0, 4) + "0" + (new Integer(month)).toString();
        }
        return (new Integer(year + 1)).toString() + "0" + (new Integer(month - 12)).toString();
    }

    /**
     * 轉化成中文格式日期
     * @param d
     * @return
     */
    public static String getChineseDate_Date(
            Date d) {
        if (d == null) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd",
                new DateFormatSymbols());
        String dtrDate = df.format(d);
        return dtrDate.substring(0, 4) + "\u5E74" + Integer.parseInt(dtrDate.substring(4, 6)) + "\u6708" + Integer.parseInt(dtrDate.substring(6, 8)) + "\u65E5";
    }

    /**
     * 當前日期
     * @return
     */
    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date d = cal.getTime();
        return d;
    }

    /**
     * 當前年月
     * @return
     */
    public static String getCurrentYearMonth() {
        Calendar cal = Calendar.getInstance();
        String currentYear = (new Integer(cal.get(1))).toString();
        String currentMonth = null;
        if (cal.get(2) < 9) {
            currentMonth = "0" + (new Integer(cal.get(2) + 1)).toString();
        } else {
            currentMonth = (new Integer(cal.get(2) + 1)).toString();
        }
        return currentYear + currentMonth;
    }

    /**
     * 字元轉化成時間
     * @param strDate 時間字串
     * @param oracleFormat 格式
     * @return
     */
    public static Date stringToDate(
            String strDate, String oracleFormat) {
        if (strDate == null) {
            return null;
        }
        Hashtable h = new Hashtable();
        String javaFormat = "";
        String s = oracleFormat.toLowerCase();
        if (s.indexOf("yyyy") != -1) {
            h.put(new Integer(s.indexOf("yyyy")), "yyyy");
        } else if (s.indexOf("yy") != -1) {
            h.put(new Integer(s.indexOf("yy")), "yy");
        }
        if (s.indexOf("mm") != -1) {
            h.put(new Integer(s.indexOf("mm")), "MM");
        }
        if (s.indexOf("dd") != -1) {
            h.put(new Integer(s.indexOf("dd")), "dd");
        }
        if (s.indexOf("hh24") != -1) {
            h.put(new Integer(s.indexOf("hh24")), "HH");
        }
        if (s.indexOf("mi") != -1) {
            h.put(new Integer(s.indexOf("mi")), "mm");
        }
        if (s.indexOf("ss") != -1) {
            h.put(new Integer(s.indexOf("ss")), "ss");
        }
        for (int intStart = 0; s.indexOf("-", intStart) != -1; intStart++) {
            intStart = s.indexOf("-", intStart);
            h.put(new Integer(intStart), "-");
        }

        for (int intStart = 0; s.indexOf("/", intStart) != -1; intStart++) {
            intStart = s.indexOf("/", intStart);
            h.put(new Integer(intStart), "/");
        }

        for (int intStart = 0; s.indexOf(" ", intStart) != -1; intStart++) {
            intStart = s.indexOf(" ", intStart);
            h.put(new Integer(intStart), " ");
        }

        for (int intStart = 0; s.indexOf(":", intStart) != -1; intStart++) {
            intStart = s.indexOf(":", intStart);
            h.put(new Integer(intStart), ":");
        }

        if (s.indexOf("\u5E74") != -1) {
            h.put(new Integer(s.indexOf("\u5E74")), "\u5E74");
        }
        if (s.indexOf("\u6708") != -1) {
            h.put(new Integer(s.indexOf("\u6708")), "\u6708");
        }
        if (s.indexOf("\u65E5") != -1) {
            h.put(new Integer(s.indexOf("\u65E5")), "\u65E5");
        }
        if (s.indexOf("\u65F6") != -1) {
            h.put(new Integer(s.indexOf("\u65F6")), "\u65F6");
        }
        if (s.indexOf("\u5206") != -1) {
            h.put(new Integer(s.indexOf("\u5206")), "\u5206");
        }
        if (s.indexOf("\u79D2") != -1) {
            h.put(new Integer(s.indexOf("\u79D2")), "\u79D2");
        }
        int i = 0;
        while (h.size() != 0) {
            Enumeration e = h.keys();
            int n = 0;
            while (e.hasMoreElements()) {
                i = ((Integer) e.nextElement()).intValue();
                if (i >= n) {
                    n = i;
                }
            }

            String temp = (String) h.get(new Integer(n));
            h.remove(new Integer(n));
            javaFormat = temp + javaFormat;
        }

        SimpleDateFormat df = new SimpleDateFormat(javaFormat);
        Date myDate = new Date();
        try {
            myDate = df.parse(strDate);
        } catch (Exception e) {
            return null;
        }

        return myDate;
    }

    /**
     * 日期轉化成字串
     * @param d 日期
     * @param format 格式
     * @return
     */
    public static String dateToString(
            Date d, String format) {
        if (d == null) {
            return "";
        }
        Hashtable h = new Hashtable();
        String javaFormat = "";
        String s = format.toLowerCase();
        if (s.indexOf("yyyy") != -1) {
            h.put(new Integer(s.indexOf("yyyy")), "yyyy");
        } else if (s.indexOf("yy") != -1) {
            h.put(new Integer(s.indexOf("yy")), "yy");
        }
        if (s.indexOf("mm") != -1) {
            h.put(new Integer(s.indexOf("mm")), "MM");
        }
        if (s.indexOf("dd") != -1) {
            h.put(new Integer(s.indexOf("dd")), "dd");
        }
        if (s.indexOf("hh24") != -1) {
            h.put(new Integer(s.indexOf("hh24")), "HH");
        }
        if (s.indexOf("mi") != -1) {
            h.put(new Integer(s.indexOf("mi")), "mm");
        }
        if (s.indexOf("ss") != -1) {
            h.put(new Integer(s.indexOf("ss")), "ss");
        }
        for (int intStart = 0; s.indexOf("-", intStart) != -1; intStart++) {
            intStart = s.indexOf("-", intStart);
            h.put(new Integer(intStart), "-");
        }

        for (int intStart = 0; s.indexOf("/", intStart) != -1; intStart++) {
            intStart = s.indexOf("/", intStart);
            h.put(new Integer(intStart), "/");
        }

        for (int intStart = 0; s.indexOf(" ", intStart) != -1; intStart++) {
            intStart = s.indexOf(" ", intStart);
            h.put(new Integer(intStart), " ");
        }

        for (int intStart = 0; s.indexOf(":", intStart) != -1; intStart++) {
            intStart = s.indexOf(":", intStart);
            h.put(new Integer(intStart), ":");
        }

        if (s.indexOf("\u5E74") != -1) {
            h.put(new Integer(s.indexOf("\u5E74")), "\u5E74");
        }
        if (s.indexOf("\u6708") != -1) {
            h.put(new Integer(s.indexOf("\u6708")), "\u6708");
        }
        if (s.indexOf("\u65E5") != -1) {
            h.put(new Integer(s.indexOf("\u65E5")), "\u65E5");
        }
        if (s.indexOf("\u65F6") != -1) {
            h.put(new Integer(s.indexOf("\u65F6")), "\u65F6");
        }
        if (s.indexOf("\u5206") != -1) {
            h.put(new Integer(s.indexOf("\u5206")), "\u5206");
        }
        if (s.indexOf("\u79D2") != -1) {
            h.put(new Integer(s.indexOf("\u79D2")), "\u79D2");
        }
        int i = 0;
        while (h.size() != 0) {
            Enumeration e = h.keys();
            int n = 0;
            while (e.hasMoreElements()) {
                i = ((Integer) e.nextElement()).intValue();
                if (i >= n) {
                    n = i;
                }
            }

            String temp = (String) h.get(new Integer(n));
            h.remove(new Integer(n));
            javaFormat = temp + javaFormat;
        }

        SimpleDateFormat df = new SimpleDateFormat(javaFormat,
                new DateFormatSymbols());
        return df.format(d);
    }

    /**
     * 格式化日期
     * @param d 日期
     * @param format 格式
     * @return
     */
    public static String getDate(
            Date d, String format) {
        if (d == null) {
            return "";
        }
        Hashtable h = new Hashtable();
        String javaFormat = "";
        String s = format.toLowerCase();
        if (s.indexOf("yyyy") != -1) {
            h.put(new Integer(s.indexOf("yyyy")), "yyyy");
        } else if (s.indexOf("yy") != -1) {
            h.put(new Integer(s.indexOf("yy")), "yy");
        }
        if (s.indexOf("mm") != -1) {
            h.put(new Integer(s.indexOf("mm")), "MM");
        }
        if (s.indexOf("dd") != -1) {
            h.put(new Integer(s.indexOf("dd")), "dd");
        }
        if (s.indexOf("hh24") != -1) {
            h.put(new Integer(s.indexOf("hh24")), "HH");
        }
        if (s.indexOf("mi") != -1) {
            h.put(new Integer(s.indexOf("mi")), "mm");
        }
        if (s.indexOf("ss") != -1) {
            h.put(new Integer(s.indexOf("ss")), "ss");
        }
        for (int intStart = 0; s.indexOf("-", intStart) != -1; intStart++) {
            intStart = s.indexOf("-", intStart);
            h.put(new Integer(intStart), "-");
        }

        for (int intStart = 0; s.indexOf("/", intStart) != -1; intStart++) {
            intStart = s.indexOf("/", intStart);
            h.put(new Integer(intStart), "/");
        }

        for (int intStart = 0; s.indexOf(" ", intStart) != -1; intStart++) {
            intStart = s.indexOf(" ", intStart);
            h.put(new Integer(intStart), " ");
        }

        for (int intStart = 0; s.indexOf(":", intStart) != -1; intStart++) {
            intStart = s.indexOf(":", intStart);
            h.put(new Integer(intStart), ":");
        }

        if (s.indexOf("\u5E74") != -1) {
            h.put(new Integer(s.indexOf("\u5E74")), "\u5E74");
        }
        if (s.indexOf("\u6708") != -1) {
            h.put(new Integer(s.indexOf("\u6708")), "\u6708");
        }
        if (s.indexOf("\u65E5") != -1) {
            h.put(new Integer(s.indexOf("\u65E5")), "\u65E5");
        }
        if (s.indexOf("\u65F6") != -1) {
            h.put(new Integer(s.indexOf("\u65F6")), "\u65F6");
        }
        if (s.indexOf("\u5206") != -1) {
            h.put(new Integer(s.indexOf("\u5206")), "\u5206");
        }
        if (s.indexOf("\u79D2") != -1) {
            h.put(new Integer(s.indexOf("\u79D2")), "\u79D2");
        }
        int i = 0;
        while (h.size() != 0) {
            Enumeration e = h.keys();
            int n = 0;
            while (e.hasMoreElements()) {
                i = ((Integer) e.nextElement()).intValue();
                if (i >= n) {
                    n = i;
                }
            }

            String temp = (String) h.get(new Integer(n));
            h.remove(new Integer(n));
            javaFormat = temp + javaFormat;
        }

        SimpleDateFormat df = new SimpleDateFormat(javaFormat,
                new DateFormatSymbols());
        return df.format(d);
    }

    public static String getCurrentDateTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        String returndatetime = sdf.format(now);
        return returndatetime;
    }

    /**
     * 比較日期1是否大於等於日期2
     * @param s1 日期1
     * @param s2 日期2
     * @return
     */
    public static boolean yearMonthGreatEqual(String s1, String s2) {
        String temp1 = s1.substring(0, 4);
        String temp2 = s2.substring(0, 4);
        String temp3 = s1.substring(4, 6);
        String temp4 = s2.substring(4, 6);
        if (Integer.parseInt(temp1) > Integer.parseInt(temp2)) {
            return true;
        }
        if (Integer.parseInt(temp1) == Integer.parseInt(temp2)) {
            return Integer.parseInt(temp3) >= Integer.parseInt(temp4);
        }
        return false;
    }

    /**
     * 獲得oracle格式的日期字串
     * @param d 日期
     * @return
     */
    public static String getOracleFormatDateStr(
            Date d) {
        return getDate(d, "YYYY-MM-DD HH24:MI:SS");
    }

    public static String getLastDay(
            String term) {
        int getYear = Integer.parseInt(term.substring(0, 4));
        int getMonth = Integer.parseInt(term.substring(4, 6));
        String getLastDay = "";
        if (getMonth == 2) {
            if (getYear % 4 == 0 && getYear % 100 != 0 || getYear % 400 == 0) {
                getLastDay = "29";
            } else {
                getLastDay = "28";
            }
        } else if (getMonth == 4 || getMonth == 6 || getMonth == 9 || getMonth == 11) {
            getLastDay = "30";
        } else {
            getLastDay = "31";
        }
        return String.valueOf(getYear) + "\u5E74" + String.valueOf(getMonth) + "\u6708" + getLastDay + "\u65E5";
    }

    /**
     * 增加月
     * @param strDate 初始年月
     * @param intDiff 增加的數量
     * @return
     */
    public static String getMonthBetween(
            String strDate, int intDiff) {
        try {
            int intYear = Integer.parseInt(strDate.substring(0, 4));
            int intMonth = Integer.parseInt(strDate.substring(4, 6));
            String strDay = "";
            if (strDate.length() > 6) {
                strDay = strDate.substring(6, strDate.length());
            }
            for (intMonth += intDiff; intMonth <= 0; intMonth += 12) {
                intYear--;
            }
            for (; intMonth > 12; intMonth -= 12) {
                intYear++;
            }
            if (intMonth < 10) {
                return Integer.toString(intYear) + "0" + Integer.toString(intMonth) + strDay;
            }

            return Integer.toString(intYear) + Integer.toString(intMonth) + strDay;
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * 計算兩個年月之間的相差月數
     * @param strDateBegin
     * @param strDateEnd
     * @return
     */
    public static String getMonthBetween(
            String strDateBegin, String strDateEnd) {
        try {
            String strOut;
            if (strDateBegin.equals("") || strDateEnd.equals("") || strDateBegin.length() != 6 || strDateEnd.length() != 6) {
                strOut = "";
            } else {
                int intMonthBegin = Integer.parseInt(strDateBegin.substring(0, 4)) * 12 + Integer.parseInt(strDateBegin.substring(4, 6));
                int intMonthEnd = Integer.parseInt(strDateEnd.substring(0, 4)) * 12 + Integer.parseInt(strDateEnd.substring(4, 6));
                strOut =
                        String.valueOf(intMonthBegin - intMonthEnd);
            }

            return strOut;
        } catch (Exception e) {
            return "0";
        }

    }

    /**
     * 將“yyyymmdd”格式的日期轉化成“yyyy-mm-dd”格式
     * @param strDate
     * @return
     */
    public static String getStrHaveAcross(
            String strDate) {
        try {
            return strDate.substring(0, 4) + "-" + strDate.substring(4, 6) + "-" + strDate.substring(6, 8);
        } catch (Exception e) {
            return strDate;
        }

    }

    /**
     * 計算當前日期的下個月的第一天
     * @return
     */
    public static String getFirstDayOfNextMonth() {
        String strToday = getCurrentDate_String();
        return increaseYearMonth(strToday.substring(0, 6)) + "01";
    }

    public static final Date valid_Str_DateTime(String ioStr, SimpleDateFormat iosdf) {
        String opStr = ioStr;
        Date opDate = null;
        try {
            opDate = iosdf.parse(opStr);
        } catch (ParseException pex) {
        }
        return opDate;
    }
    //calcuate day of week by date

    public static final Integer calcuate_Week(String year, String month, String day)
            throws Exception {
        int opday, opmonth, opmonth2, opyear, tmpval1, tmpval2, tmpval3, tmpval4, tmpval5, tmpval6, tmpval7;
        int rtweek = -1;
        opday = Integer.parseInt(day);
        opmonth = Integer.parseInt(month);
        opmonth2 = Integer.parseInt(month);
        opyear = Integer.parseInt(year);

        if (opmonth == 1) {
            opmonth2 = 13;
            opyear -= 1;
        }
        if (opmonth == 2) {
            opmonth2 = 14;
            opyear -= 1;
        }
        tmpval1 = (int) (((opmonth2 + 1) * 3) / 5);
        tmpval2 = (int) (opyear / 4);
        tmpval3 = (int) (opyear / 100);
        tmpval4 = (int) (opyear / 400);
        tmpval5 = opday + (opmonth2 * 2) + tmpval1 + opyear + tmpval2 - tmpval3 + tmpval4 + 2;
        tmpval6 = (int) (tmpval5 / 7);
        tmpval7 = tmpval5 - (tmpval6 * 7);
        rtweek = tmpval7 - 1;
        if (rtweek < 0) {
            rtweek = 6;
        }

        return rtweek;
    }
    //calcuate week of year by date

    public static final Integer calcuate_Week_byDay(String year, String month, String day) {
        int opyear, opmonth, opday, rtweek;
        opyear = Integer.parseInt(year);
        opmonth = Integer.parseInt(month) - 1;
        opday = Integer.parseInt(day);

        Calendar cal = new GregorianCalendar();

        cal.set(opyear, opmonth, opday);
        rtweek = cal.get(Calendar.WEEK_OF_YEAR);

        return rtweek;
    }

    public static final String calcuate_Before_Month_with_datetime(String datetime, int month) {
        int year1, month1, day1, hour1, minutes1;
        year1 = Integer.parseInt(datetime.substring(0, 4));
        month1 = Integer.parseInt(datetime.substring(5, 7)) - 1;
        day1 = Integer.parseInt(datetime.substring(8, 10));
        hour1 = Integer.parseInt(datetime.substring(11, 13));
        minutes1 = Integer.parseInt(datetime.substring(14, 16));

        Calendar ca = Calendar.getInstance();
        ca.set(year1, month1, day1, hour1, minutes1, 00);


        ca.add(Calendar.MONTH, -month);

        Date now = new Date(ca.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(now);
    }

    public static final String calcuate_Before_Month(int month) {
        Calendar ca = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();

        ca.setTime(nowdate);
        ca.add(Calendar.MONTH, -month);

        Date now = new Date(ca.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT5);

        return sdf.format(now);
    }

    public static final Date calcuateDate_Before_Day(int day) {
        Calendar ca = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.setTime(nowdate);
        ca.set(Calendar.DAY_OF_YEAR, ca.get(Calendar.DAY_OF_YEAR) - day);
        Date now = new Date(ca.getTimeInMillis());

        return now;
    }

    public static final String calcuate_Before_Day(int day) {
        Calendar ca = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.setTime(nowdate);
        ca.set(Calendar.DAY_OF_YEAR, ca.get(Calendar.DAY_OF_YEAR) - day);
        Date now = new Date(ca.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(now);
    }

    public static final String calcuate_After_Day(String timestamp, int day) {
        int year1, year2, month1, month2, day1, day2, hour1, hour2, minuts1, minuts2, second1, second2;
        year1 = Integer.parseInt(timestamp.substring(0, 4));
        month1 = Integer.parseInt(timestamp.substring(5, 7)) - 1;
        day1 = Integer.parseInt(timestamp.substring(8, 10));
        hour1 = Integer.parseInt(timestamp.substring(11, 13));
        minuts1 = Integer.parseInt(timestamp.substring(14, 16));
        second1 = Integer.parseInt(timestamp.substring(17, 19));

        Calendar ca = Calendar.getInstance();
        ca.set(year1, month1, day1, hour1, minuts1, second1);

        ca.set(Calendar.DAY_OF_YEAR, ca.get(Calendar.DAY_OF_YEAR) + day);
        Date now = new Date(ca.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(now);
    }

    public static final String calcuate_Before_Hours(int hours) {
        Calendar ca = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.setTime(nowdate);
        ca.set(Calendar.HOUR_OF_DAY, ca.get(Calendar.HOUR_OF_DAY) - hours);
        Date now = new Date(ca.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(now);
    }

    public static final String calcuate_Before_Month(String timestamp, int cal_month) {
        int year, month, day, hour;
        year = Integer.parseInt(timestamp.substring(0, 4));
        month = Integer.parseInt(timestamp.substring(5, 7)) - 1;
        day = Integer.parseInt(timestamp.substring(8, 10));
        hour = Integer.parseInt(timestamp.substring(11, 13));

        Calendar ca = Calendar.getInstance();
        ca.set(year, month, day, 00, 00);
        ca.add(Calendar.MONTH, -cal_month);

        Date now = new Date(ca.getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT5);

        return sdf.format(now);
    }

    public static final Boolean isAfterDate_ByNow(String timestamp) {
        int year, month, day, hour;
        year = Integer.parseInt(timestamp.substring(0, 4));
        month = Integer.parseInt(timestamp.substring(5, 7)) - 1;
        day = Integer.parseInt(timestamp.substring(8, 10));
        hour = Integer.parseInt(timestamp.substring(11, 13));

        Calendar ca = Calendar.getInstance();
        Calendar ca2 = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.setTime(nowdate);
        ca2.set(year, month, day, hour, 00);

        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        return ca.after(ca2);
    }

    public static final Boolean isAfterDate_ByDate(String indatetime_1, String indatetime_2) {

        int year1, year2, month1, month2, day1, day2, hour1, hour2;
        year1 = Integer.parseInt(indatetime_1.substring(0, 4));
        month1 = Integer.parseInt(indatetime_1.substring(5, 7)) - 1;
        day1 = Integer.parseInt(indatetime_1.substring(8, 10));
        hour1 = Integer.parseInt(indatetime_1.substring(11, 13));

        year2 = Integer.parseInt(indatetime_2.substring(0, 4));
        month2 = Integer.parseInt(indatetime_2.substring(5, 7)) - 1;
        day2 = Integer.parseInt(indatetime_2.substring(8, 10));
        hour2 = Integer.parseInt(indatetime_2.substring(11, 13));

        Calendar ca = Calendar.getInstance();
        Calendar ca2 = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.set(year1, month1, day1, hour1, 00);
        ca2.set(year2, month2, day2, hour2, 00);

        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        return ca.after(ca2);
    }

    public static final Boolean isAfterDate_ByDate2(String indatetime_1, String indatetime_2) {

        int year1, year2, month1, month2, day1, day2, hour1, hour2;
        year1 = Integer.parseInt(indatetime_1.substring(0, 4));
        month1 = Integer.parseInt(indatetime_1.substring(5, 7)) - 1;
        day1 = Integer.parseInt(indatetime_1.substring(8, 10));


        year2 = Integer.parseInt(indatetime_2.substring(0, 4));
        month2 = Integer.parseInt(indatetime_2.substring(5, 7)) - 1;
        day2 = Integer.parseInt(indatetime_2.substring(8, 10));


        Calendar ca = Calendar.getInstance();
        Calendar ca2 = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.set(year1, month1, day1, 00, 00);
        ca2.set(year2, month2, day2, 00, 00);

        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        return ca.after(ca2);
    }

    public static final Date toDate(String DateTime) {
        int year, month, day, hour, minuts, second;
        year = Integer.parseInt(DateTime.substring(0, 4));
        month = Integer.parseInt(DateTime.substring(5, 7)) - 1;
        day = Integer.parseInt(DateTime.substring(8, 10));
        hour = Integer.parseInt(DateTime.substring(11, 13));
        minuts = Integer.parseInt(DateTime.substring(14, 16));
        second = Integer.parseInt(DateTime.substring(17, 19));
        Calendar ca = Calendar.getInstance();
        ca.set(year, month, day, hour, minuts, second);
        long time = ca.getTimeInMillis();
        Date date = new Date(time);

        return date;
    }

    public static final Boolean isAfterTime_ByDate(String indatetime_1, String indatetime_2) {

        int year1, year2, month1, month2, day1, day2, hour1, hour2, minuts1, minuts2, second1, second2;
        year1 = Integer.parseInt(indatetime_1.substring(0, 4));
        month1 = Integer.parseInt(indatetime_1.substring(5, 7)) - 1;
        day1 = Integer.parseInt(indatetime_1.substring(8, 10));
        hour1 = Integer.parseInt(indatetime_1.substring(11, 13));
        minuts1 = Integer.parseInt(indatetime_1.substring(14, 16));
        second1 = Integer.parseInt(indatetime_1.substring(17, 19));

        year2 = Integer.parseInt(indatetime_2.substring(0, 4));
        month2 = Integer.parseInt(indatetime_2.substring(5, 7)) - 1;
        day2 = Integer.parseInt(indatetime_2.substring(8, 10));
        hour2 = Integer.parseInt(indatetime_2.substring(11, 13));
        minuts2 = Integer.parseInt(indatetime_2.substring(14, 16));
        second2 = Integer.parseInt(indatetime_2.substring(17, 19));

        Calendar ca = Calendar.getInstance();
        Calendar ca2 = Calendar.getInstance();
        java.util.Date nowdate = new java.util.Date();
        ca.set(year1, month1, day1, hour1, minuts1, second1);
        ca2.set(year2, month2, day2, hour2, minuts2, second2);

        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);
        return ca.after(ca2);
    }

    public static final Date calcuate_after_time(Date date, long time) throws Exception {

        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        long starttime = ca.getTimeInMillis();
        long endtime = starttime + time;
        Date enddate = new Date(endtime);

        return enddate;
    }

    public static final String calcuate_after_time(String indatetime, long time) throws Exception {
        int year1, month1, day1, hour1, minutes1;
        year1 = Integer.parseInt(indatetime.substring(0, 4));
        month1 = Integer.parseInt(indatetime.substring(5, 7)) - 1;
        day1 = Integer.parseInt(indatetime.substring(8, 10));
        hour1 = Integer.parseInt(indatetime.substring(11, 13));
        minutes1 = Integer.parseInt(indatetime.substring(14, 16));

        Calendar ca = Calendar.getInstance();
        ca.set(year1, month1, day1, hour1, minutes1, 00);
        long starttime = ca.getTimeInMillis();
        long endtime = starttime + time;
        Date enddate = new Date(endtime);
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(enddate);
    }

    public static final String calcuate_after_time1(String indatetime, long time) throws Exception {
        int year1, month1, day1, hour1, minutes1, second1;
        year1 = Integer.parseInt(indatetime.substring(0, 4));
        month1 = Integer.parseInt(indatetime.substring(5, 7)) - 1;
        day1 = Integer.parseInt(indatetime.substring(8, 10));
        hour1 = Integer.parseInt(indatetime.substring(11, 13));
        minutes1 = Integer.parseInt(indatetime.substring(14, 16));
        second1 = Integer.parseInt(indatetime.substring(17, 19));
        System.out.println(" ** indatetime = " + indatetime + ", hour1 = " + hour1 + ", minutes1 = " + minutes1 + ", second1 = " + second1);

        Calendar ca = Calendar.getInstance();
        ca.set(year1, month1, day1, hour1, minutes1, second1);
        long starttime = ca.getTimeInMillis();
        long endtime = starttime + time;
        Date enddate = new Date(endtime);
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(enddate);
    }

    public static final String calcuate_before_time(String indatetime, long time) {
        int year1, month1, day1, hour1, minutes1, second1;
        year1 = Integer.parseInt(indatetime.substring(0, 4));
        month1 = Integer.parseInt(indatetime.substring(5, 7)) - 1;
        day1 = Integer.parseInt(indatetime.substring(8, 10));
        hour1 = Integer.parseInt(indatetime.substring(11, 13));
        minutes1 = Integer.parseInt(indatetime.substring(14, 16));
        second1 = Integer.parseInt(indatetime.substring(17, 19));

        Calendar ca = Calendar.getInstance();
        ca.set(year1, month1, day1, hour1, minutes1, second1);
        long starttime = ca.getTimeInMillis();
        long endtime = starttime - time;
        Date enddate = new Date(endtime);
        SimpleDateFormat sdf = new SimpleDateFormat(ShareParm.PARM_DATEFORMAT);

        return sdf.format(enddate);
    }

    /**
     *取得當月天數
     */
    public static final int getDaysOfCurrentMonth() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);

        a.roll(Calendar.DATE, -1);

        int MaxDate = a.get(Calendar.DATE);
        return MaxDate;
    }

    /**
     *取得指定月天數
     */
    public static final int getDaysOfMonth(Date date) {
        Calendar a = Calendar.getInstance();
        a.setTime(date);
        a.set(Calendar.DATE, 1);

        a.roll(Calendar.DATE, -1);

        int MaxDate = a.get(Calendar.DATE);
        return MaxDate;
    }

    public static final Timestamp getSqlTimestamp_byNow() {
        java.sql.Timestamp result = new java.sql.Timestamp(new java.util.Date().getTime());

        return result;
    }

    public static final Timestamp getSqlTimestamp_byDate(Date date) {
        java.sql.Timestamp result = new java.sql.Timestamp(date.getTime());
        return result;
    }

    public static final java.sql.Date getSqlDate_byDate(Date date) {
        java.sql.Date result = new java.sql.Date(date.getTime());
        return result;
    }

    public static final Timestamp getSqlTimestamp_byDate(String date) {

        java.sql.Timestamp result = new java.sql.Timestamp(toDate(date).getTime());
        return result;
    }

    public static final Date getDate_bySqlTImestamp(java.sql.Timestamp timestamp) {
        Date result = new Date(timestamp.getTime());
        return result;

    }

    /**
     * 返回日期依照所指定的格式
     *
     * @param dateformat
     *            日期格式
     * ex.(yyyy-MM-dd HH:mm:ss)
     * (yyyy-MM-dd 12:mm:ss)
     * (yyyy-MM-dd HH:30:ss)
     * @return 日期(2008-09-15 12:00:00)
     */
    public static final String getDate_With_DateFormat(String dateformat) {
        String rtresult = null;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
        SimpleDateFormat sdf_minute = new SimpleDateFormat("mm");
        SimpleDateFormat sdf_second = new SimpleDateFormat("ss");
        String tmpStr = "";
        if (dateformat.indexOf("yyyy") != -1) {
            tmpStr += sdf_year.format(now);
        } else {
            tmpStr += dateformat.substring(0, 4);
        }
        if (dateformat.indexOf("MM") != -1) {
            tmpStr += sdf_month.format(now);
        } else {
            tmpStr += dateformat.substring(5, 7);
        }
        if (dateformat.indexOf("dd") != -1) {
            tmpStr += sdf_day.format(now);
        } else {
            tmpStr += dateformat.substring(8, 10);
        }
        if (dateformat.indexOf("HH") != -1) {
            tmpStr += sdf_hour.format(now);
        } else {
            tmpStr += dateformat.substring(11, 13);
        }
        if (dateformat.indexOf("mm") != -1) {
            tmpStr += sdf_minute.format(now);
        } else {
            tmpStr += dateformat.substring(14, 16);
        }
        if (dateformat.indexOf("ss") != -1) {
            tmpStr += sdf_second.format(now);
        } else {
            tmpStr += dateformat.substring(17, 19);
        }

        try {
            int weekofday = Integer.valueOf(dateformat.substring(20, 21));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, weekofday + 1);
            return sdf.format(cal.getTime());
        } catch (Exception ex) {
        }

        try {
            rtresult = sdf.format(sdf2.parse(tmpStr));
        } catch (Exception ex) {
        }
        return rtresult;
    }

    /**
     * 返回日期依照所指定的格式
     *
     * @param dateformat
     *            日期格式
     * ex.(yyyyMMddHHmmssE)
     * (yyyyMMdd12mmssE)
     * (yyyyMMddHH30ssE)
     * (yyyyMMddHHss2)
     * @return 日期(2008-09-15 12:00:00)
     */
    public static final String getDate_With_DateFormat_2(String dateformat) {
        String rtresult = null;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
        SimpleDateFormat sdf_minute = new SimpleDateFormat("mm");
        SimpleDateFormat sdf_second = new SimpleDateFormat("ss");
        String tmpStr = "";
        if (dateformat.indexOf("yyyy") != -1) {
            tmpStr += sdf_year.format(now);
        } else {
            tmpStr += dateformat.substring(0, 4);
        }
        if (dateformat.indexOf("MM") != -1) {
            tmpStr += sdf_month.format(now);
        } else {
            tmpStr += dateformat.substring(4, 6);
        }
        if (dateformat.indexOf("dd") != -1) {
            tmpStr += sdf_day.format(now);
        } else {
            tmpStr += dateformat.substring(6, 8);
        }
        if (dateformat.indexOf("HH") != -1) {
            tmpStr += sdf_hour.format(now);
        } else {
            tmpStr += dateformat.substring(8, 10);
        }
        if (dateformat.indexOf("mm") != -1) {
            tmpStr += sdf_minute.format(now);
        } else {
            tmpStr += dateformat.substring(10, 12);
        }
        if (dateformat.indexOf("ss") != -1) {
            tmpStr += sdf_second.format(now);
        } else {
            tmpStr += dateformat.substring(12, 14);
        }

        try {
            int weekofday = Integer.valueOf(dateformat.substring(14, 15));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, weekofday + 1);
            return sdf.format(cal.getTime());
        } catch (Exception ex) {
        }

        try {
            rtresult = sdf.format(sdf2.parse(tmpStr));
        } catch (Exception ex) {
        }
        return rtresult;
    }

    /**
     * 返回日期依照所指定的格式
     *
     * @param dateformat
     *            日期格式
     * ex.(yyyyMMddHHmmssE)
     * (yyyyMMdd12mmssE)
     * (yyyyMMddHH30ssE)
     * (yyyyMMddHHss2)
     * @return 日期(2008-09-15 12:00:00)
     */
    public static final String getDate_With_DateFormatWeek(String dateformat) {
        String rtresult = null;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
        SimpleDateFormat sdf_minute = new SimpleDateFormat("mm");
        SimpleDateFormat sdf_second = new SimpleDateFormat("ss");
        String tmpStr = "";
        if (dateformat.indexOf("yyyy") != -1) {
            tmpStr += sdf_year.format(now);
        } else {
            tmpStr += dateformat.substring(0, 4);
        }
        if (dateformat.indexOf("MM") != -1) {
            tmpStr += sdf_month.format(now);
        } else {
            tmpStr += dateformat.substring(4, 6);
        }
        if (dateformat.indexOf("dd") != -1) {
            tmpStr += sdf_day.format(now);
        } else {
            tmpStr += dateformat.substring(6, 8);
        }
        if (dateformat.indexOf("HH") != -1) {
            tmpStr += sdf_hour.format(now);
        } else {
            tmpStr += dateformat.substring(8, 10);
        }
        if (dateformat.indexOf("mm") != -1) {
            tmpStr += sdf_minute.format(now);
        } else {
            tmpStr += dateformat.substring(10, 12);
        }
        try {
            if (dateformat.indexOf("ss") != -1) {
                tmpStr += sdf_second.format(now);
            } else {
                tmpStr += dateformat.substring(12, 14);
            }
        } catch (Exception ex) {
        }
        try {
            int weekofday = Integer.valueOf(dateformat.substring(14, 15));
            Calendar cal = Calendar.getInstance();

            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, weekofday + 1);
            if (dateformat.length() == 15) {
                if (dateformat.indexOf("HH") == -1) {
                    int hour = Integer.valueOf(dateformat.substring(8, 10));
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                }
                if (dateformat.indexOf("mm") == -1) {
                    int minute = Integer.valueOf(dateformat.substring(10, 12));
                    cal.set(Calendar.MINUTE, minute);
                }
                if (dateformat.indexOf("s") == -1) {
                    int second = Integer.valueOf(dateformat.substring(12, 14));
                    cal.set(Calendar.SECOND, second);
                }
            }
            Calendar ca3 = Calendar.getInstance();
            // ca3.set(YEAR, YEAR)
            //System.out.println("@@@ca3.getTime():"+ca3.getTime());

            return sdf.format(cal.getTime());
        } catch (Exception ex) {
        }

        try {
            rtresult = sdf.format(sdf2.parse(tmpStr));
        } catch (Exception ex) {
        }
        return rtresult;
    }

    public static final Date getDateWithDateFormat(String dateformat) {
        Date rtresult = null;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
        SimpleDateFormat sdf_minute = new SimpleDateFormat("mm");
        SimpleDateFormat sdf_second = new SimpleDateFormat("ss");
        String tmpStr = "";
        if (dateformat.indexOf("yyyy") != -1) {
            tmpStr += sdf_year.format(now);
        } else {
            tmpStr += dateformat.substring(0, 4);
        }
        if (dateformat.indexOf("MM") != -1) {
            tmpStr += sdf_month.format(now);
        } else {
            tmpStr += dateformat.substring(4, 6);
        }
        if (dateformat.indexOf("dd") != -1) {
            tmpStr += sdf_day.format(now);
        } else {
            tmpStr += dateformat.substring(6, 8);
        }
        if (dateformat.indexOf("HH") != -1) {
            tmpStr += sdf_hour.format(now);
        } else {
            tmpStr += dateformat.substring(8, 10);
        }
        if (dateformat.indexOf("mm") != -1) {
            tmpStr += sdf_minute.format(now);
        } else {
            tmpStr += dateformat.substring(10, 12);
        }
        if (dateformat.indexOf("ss") != -1) {
            tmpStr += sdf_second.format(now);
        } else {
            tmpStr += dateformat.substring(12, 14);
        }

        try {
            int weekofday = Integer.valueOf(dateformat.substring(14, 15));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_WEEK, weekofday + 1);
            if (dateformat.length() == 15) {
                if (dateformat.indexOf("HH") == -1) {
                    int hour = Integer.valueOf(dateformat.substring(8, 10));
                    cal.set(Calendar.HOUR, hour);
                }
                if (dateformat.indexOf("mm") == -1) {
                    int minute = Integer.valueOf(dateformat.substring(10, 12));
                    cal.set(Calendar.MINUTE, minute);
                }
                if (dateformat.indexOf("s") == -1) {
                    int second = Integer.valueOf(dateformat.substring(12, 14));
                    cal.set(Calendar.SECOND, second);
                }
            }
            return cal.getTime();
        } catch (Exception ex) {
        }

        try {
            rtresult = sdf2.parse(tmpStr);
        } catch (Exception ex) {
        }
        return rtresult;
    }

    public static void main(String[] args) throws Exception {
        DateUtil dateHelper = DateUtil.getInstance();

        /* Year */
//        for (int i = -5; i <= 5; i++) {
//            System.out.println("FirstDayOfYear(" + i + ") = " +
//                    dateHelper.getFirstDayOfYear(i));
//            System.out.println("LastDayOfYear(" + i + ") = " +
//                    dateHelper.getLastDayOfYear(i));
//        }
//
//        /* Quarter */
//        for (int i = -5; i <= 5; i++) {
//            System.out.println("FirstDayOfQuarter(" + i + ") = " +
//                    dateHelper.getFirstDayOfQuarter(i));
//            System.out.println("LastDayOfQuarter(" + i + ") = " +
//                    dateHelper.getLastDayOfQuarter(i));
//        }
//
//        /* Month */
//        for (int i = -5; i <= 5; i++) {
//            System.out.println("FirstDayOfMonth(" + i + ") = " +
//                    dateHelper.getFirstDayOfMonth(i));
//            System.out.println("LastDayOfMonth(" + i + ") = " +
//                    dateHelper.getLastDayOfMonth(i));
//        }
//
//        /* Week */
//        for (int i = -5; i <= 5; i++) {
//            System.out.println("FirstDayOfWeek(" + i + ") = " +
//                    dateHelper.getFirstDayOfWeek(i));
//            System.out.println("LastDayOfWeek(" + i + ") = " +
//                    dateHelper.getLastDayOfWeek(i));
//        }
//
//        /* Tendays */
//        for (int i = -5; i <= 5; i++) {
//            System.out.println("FirstDayOfTendays(" + i + ") = " +
//                    dateHelper.getFirstDayOfTendays(i));
//            System.out.println("LastDayOfTendays(" + i + ") = " +
//                    dateHelper.getLastDayOfTendays(i));
//        }
//
//        System.out.println(getFirstDay("2009", "02"));
//        System.out.println(getLastDay("2009", "02"));
//
//        System.out.println("@@@@:" + new DateUtil(new Date()).getDayOfMonth(0));
//        System.out.println("@@@@:" + new DateUtil(new Date()).getDayOfMonth(1));
//        System.out.println("@@@@:" + new DateUtil(new Date()).getDayOfMonth(2));
//        System.out.println("@@@@:" + getFirstDay("2009", "9"));
//
//        System.out.println("@@@@@:" + calcuate_Before_Month_with_datetime("2009-11-01 11:20:12", -2));
//
//        System.out.println("@@@@@:" + calcuate_After_Day("2009-11-01 11:20:12", -2));

        System.out.println("@@@@@@@:" + daysBetweenDates(DateUtil.toDate("2014-07-01 00:00:00"),DateUtil.toDate("2014-07-01 00:00:00")));

        //   System.out.println("@@@@:" + toOracleDate(new Date(), "yyyy-MM-dd hh:mm:ss", "yyyy-mm-dd hh24:mi:ss"));

        // System.out.println("@@@@:" + calcuate_after_time(new Date(), 1000 * 60 * 2880));
    }
}
