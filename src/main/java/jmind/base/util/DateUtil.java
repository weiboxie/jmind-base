package jmind.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  yyyy-MM-dd HH:mm:ss.S z  2007-08-24 08:00:00.0 CST
 */
public class DateUtil {

    public static final long MINUTE = 60000;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    // yyyy-MM-dd hh:mm:ss 小写12小时制   yyyy-MM-dd HH:mm:ss  大写H，24小时制
    public final static String DEFAULT_PATTERN = "yyyyMMdd";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public final static String F24_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<Map<String, SimpleDateFormat>> _simpleDateFormat = new ThreadLocal<Map<String, SimpleDateFormat>>() {
        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new ConcurrentHashMap<String, SimpleDateFormat>();
        }
    };

    /**
     * 时间差
     * @param begin
     * @param end
     * @param field @see Calendar.DATE
     * @return
     */
    public static int diffTime(Date begin,Date end,int field){
        long diff=end.getTime()-begin.getTime();
        switch (field){
            case Calendar.DATE:
                return (int) (diff/DAY);
            case Calendar.HOUR:
                return (int) (diff/HOUR);
            case Calendar.MINUTE:
                return (int) (diff/MINUTE);
            case Calendar.SECOND:
                return (int) (diff/1000);
            default: return (int) diff;
        }

    }

    /**
     * 某天开始时间
     * @param date 为null ，取当前时间
     * @return
     */
    public static Date getStartOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        if(date!=null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    /**
     * 某天结束时间
     * @param date 为null，取当前时间
     * @return
     */
    public static Date getEndOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        if(date!=null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    /**
     * 获取某月第一天
     * @param date  时间
     * @param month  加的月份，例如 当月就是0 ，下一个月 就是 1
     * @return
     */
    public static Date  getFristDayOfMonth(Date date,int month){
        Calendar calendar = Calendar.getInstance();
        if(date!=null) {
            calendar.setTime(date);
        }
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一个月的最后一天
     * @param date
     * @param month
     * @return
     */
    public static Date  getLastDayOfMonth(Date date,int month){
        Calendar calendar = Calendar.getInstance();
        if(date!=null) {
            calendar.setTime(date);
        }
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.MILLISECOND,999);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        return  calendar.getTime();

    }



    public static String Today = getToday();

    public static int getCurrentSeconds() {
        return  (int)(System.currentTimeMillis() / 1000);
    }

    /**
     * 当前年
     * @return
     */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static List<Integer> getNearYears(int before, int after) {
        List<Integer> years = new ArrayList<Integer>();
        int start = getCurrentYear() - before;
        for (int i = 0; i <= (before + after); i++) {
            years.add(start + i);
        }
        return years;
    }

    public static String format(Date time) {
        return getFormat(DEFAULT_PATTERN).format(time);
    }

    public static String format(Date time, String formart) {
        return getFormat(formart).format(time);
    }

    public static String format(long time) {
        return getFormat(DEFAULT_PATTERN).format(time);
    }

    public static String format(long currentTime, long time) {
        int min = (int) ((currentTime - time) / MINUTE);
        if (min <= 0)
            return "刚刚";
        if (min < 60)
            return min + "分钟前";
        if (min < 24 * 60)
            return (min / 60) + "小时前";
        return getFormat("M月d日 H:mm").format(time);
    }

    public static void setToday() {
        Today = getToday();
    }

    /**
     * 判断是否为当天
     * 支持  yyyyMMdd  yyyyMMdd hh:mm:ss 两种格式
     * @param date
     * @return
     */
    public static boolean isToday(String date) {
        if (DataUtil.isEmpty(date))
            return false;
        date = date.split(" ")[0];
        date = date.replace("-", "");
        return date.equals(getToday());
    }

    /**
     * 给外部使用，mopper直接用 Today 变量
     * @return
     */
    public static String getToday() {
        return getFormat(DEFAULT_PATTERN).format(new Date());
    }


    public static int getDayInt(Date date) {
        return date == null ? 0 : getDayInt(date.getTime());
    }

    /**
     * 得到从1970年1月1日到此日期的天数<br>
     * 可以利用返回值进行日期间隔的比较<br>
     * <br>
     * 适用于不需要构造Date对象的情况，如使用System.currentTimeMillis作为参数
     */
    public static int getDayInt(long time) {
        return (int) (time / DAY);
    }

    /**
     * 得到多少分钟的int 值
     * @param time  时间
     * @param m  几分钟
     * @return
     */
    public static int getMinuteInt(long time, int m) {
        return (int) (time / (MINUTE * m));
    }




    /**
     * 增加时间
     * @param date 为null 为当前时间
     * @param field  @see Calendar.DATE
     * @param amount  时长
     * @return
     */
    public static Date add(Date date,int field, int amount){
        Calendar calendar = Calendar.getInstance();
        if(date!=null)
            calendar.setTime(date);
        calendar.add(field,  amount);
        return calendar.getTime();
    }


//    public static Calendar addDay(int days) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, days);
//        return calendar;
//    }
//
//    public static Date addDay(Date date, int days) {
//        return new Date(date.getTime() + days * DAY);
//    }

    /**
     * 往前或者后 推多少天, 返回字符串格式yyyyMMdd
     * @param date yyyyMMdd
     * @param days
     * @return
     */
    public static String addDay(String date, int days) {
        Date d = parse(date, DEFAULT_PATTERN);
        Date date2 = add(d, Calendar.DATE,days);
        return getFormat(DEFAULT_PATTERN).format(date2);
    }



    public static String getLocaleTime() {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.CHINA);
        return dateFormat.format(date);
    }

    public static String getMonthDir() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/";
    }

    public static String getDayDir() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.DATE) + "/";
    }

    /**
     * 解析时间
     * 
     * @param time
     *            表示时间的字符串，如"2009-09-15"
    
     * @return 如解析失败，返回new Date(0)
     */
    public static Date parse(String time) {
        return parse(time, F24_PATTERN, DATE_PATTERN, DEFAULT_PATTERN, "MM/dd/yyyy", "MM/dd/yyyy HH:mm:ss");
    }

    /**
     * 使用若干种规则解析时间
     * 
     * parse(String, String)
     */
    public static Date parse(String time, String... forms) {
        for (String form : forms) {
            if (DataUtil.isEmpty(form))
                continue;
            SimpleDateFormat format = getFormat(form);
            try {
                return format.parse(time);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    /**
     * 由于SimpleDateFormat很常用，但并不是线程安全，每次new一个出来又有点费
     * 此函数使用ThreadLocal方式缓存SimpleDateFormat，保证性能前提下较好地解决了问题
     */
    public static SimpleDateFormat getFormat(String form) {
        Map<String, SimpleDateFormat> formatMap = _simpleDateFormat.get();
        if (formatMap.containsKey(form)) {
            return formatMap.get(form);
        } else {
            SimpleDateFormat format = new SimpleDateFormat(form);
            formatMap.put(form, format);
            return format;
        }
    }

}
