package jmind.base.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 阴历，阳历转换
 */
public class LunarUtils {
    private GregorianCalendar calendar;

    public static void main(String[] args) {
        LunarUtils date = new LunarUtils();
        System.out.println(date);
        System.out.println(date.getLunarCalendar());
    }

    public LunarUtils() {
        calendar = new GregorianCalendar(Locale.CHINA);
    }

    /**
     * 根据格式字符串返回星期的字符串描述
     *
     * @param format
     *            CW 中文星期 EW 英文星期
     * @return
     */
    public String getWeek(String format) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] week = new String[] { "日", "一", "二", "三", "四", "五", "六" };
        String[] eweek = new String[] { "Sun.", "Mon.", "Tues.", "Wed.", "Thurs.", "Fri.", "Sat." };
        format = format.replaceAll("CW", week[dayOfWeek - 1]);
        format = format.replaceAll("EW", eweek[dayOfWeek - 1]);
        return format;
    }

    //返回默认格式的星期描述
    public String getWeek() {
        return this.getWeek("星期CW");
    }

    //返回默认格式的日期字符串
    public String getDate() {
        return this.getDateTime("yyyy-MM-dd");
    }

    //返回默认格式的时间字符串
    public String getTime() {
        return this.getDateTime("hh:mm:ss");
    }

    //返回默认格式的日期时间字符串
    public String getDateTime() {
        return this.getDateTime("yyyy-MM-dd hh:mm:ss");
    }

    //返回以格式字符串描述的日期时间字符串
    public String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    //返回默认格式的字符串描述
    @Override
    public String toString() {
        return this.getDateTime() + " " + this.getWeek() + " " + this.getLunarCalendar();
    }

    //返回默认格式的阴历字符串
    public String getLunarCalendar() {
        // TODO Auto-generated method stub
        return this.getLunarCalendar("LY年(LA)LM月LDLH时");
    }

    /**
     * 返回格式字符串描述的格式阴历字符串
     *
     * @param format
     *            LY年 LM月 LA属性 LD日 LH时区
     * @return 
     */
    public String getLunarCalendar(String format) {
        StringBuffer ret = new StringBuffer();
        String[] data = this.getLunarCalendars();

        String temp = format.replace("LY", data[0]);

        temp = temp.replaceAll("LA", data[1]);

        temp = temp.replaceAll("LM", data[2]);
        temp = temp.replaceAll("LD", data[3]);
        temp = temp.replaceAll("LH", data[4]);
        ret.append(temp);
        return ret.toString();
    }

    // 阳历数据计算
    protected String[] getLunarCalendars() {
        int[] CalendarData = new int[20];
        int[] madd = new int[12];
        String[] temp = new String[5];
        CalendarData[0] = 0x41A95;
        CalendarData[1] = 0xD4A;
        CalendarData[2] = 0xDA5;
        CalendarData[3] = 0x20B55;
        CalendarData[4] = 0x56A;
        CalendarData[5] = 0x7155B;
        CalendarData[6] = 0x25D;
        CalendarData[7] = 0x92D;
        CalendarData[8] = 0x5192B;
        CalendarData[9] = 0xA95;
        CalendarData[10] = 0xB4A;
        CalendarData[11] = 0x416AA;
        CalendarData[12] = 0xAD5;
        CalendarData[13] = 0x90AB5;
        CalendarData[14] = 0x4BA;
        CalendarData[15] = 0xA5B;
        CalendarData[16] = 0x60A57;
        CalendarData[17] = 0x52B;
        CalendarData[18] = 0xA93;
        CalendarData[19] = 0x40E95;
        //
        madd[0] = 0;
        madd[1] = 31;
        madd[2] = 59;
        madd[3] = 90;
        madd[4] = 120;
        madd[5] = 151;
        madd[6] = 181;
        madd[7] = 212;
        madd[8] = 243;
        madd[9] = 273;
        madd[10] = 304;
        madd[11] = 334;
        //
        String[] tgString = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
        String[] dzString = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
        String[] numString = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
        String[] monString = { "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊" };
        String[] sx = { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
        //
        int k = 0, m = 0, n = 0;
        boolean isEnd = false;
        int year = this.calendar.get(Calendar.YEAR);
        int month = this.calendar.get(Calendar.MONTH);
        int day = this.calendar.get(Calendar.DAY_OF_MONTH);
        int hour = this.calendar.get(Calendar.HOUR_OF_DAY);
        if (year < 1900) {
            year = 1900;
        }
        int total = (int) ((year - 2001) * 365 + Math.floor((year - 2001) / 4) + madd[month] + day - 23);
        if (year % 4 == 0 && month > 1) {
            total++;
        }
        for (m = 0;; m++) {
            k = (CalendarData[m] < 0xfff) ? 11 : 12;
            for (n = k; n >= 0; n--) {
                if (total <= 29 + getBit(CalendarData[m], n)) {
                    isEnd = true;
                    break;
                }
                total = total - 29 - getBit(CalendarData[m], n);
            }
            if (isEnd) {
                break;
            }
        }
        int c_year = 2001 + m;
        int c_month = k - n + 1;
        int cDay = total;
        if (k == 12) {
            if (c_month == Math.floor(CalendarData[m] / 0x10000) + 1)
                c_month = 1 - c_month;
            if (c_month > Math.floor(CalendarData[m] / 0x10000) + 1)
                c_month--;
        }
        double c_hour = Math.floor((hour + 3) / 2);
        //
        String tmp = tgString[(c_year - 4) % 10]; // 年干
        tmp += (dzString[(c_year - 4) % 12]); // 年支
        temp[0] = new String(tmp); // c_year
        tmp = sx[(c_year - 4) % 12];
        temp[1] = new String(tmp); // c_attrib
        tmp = "";
        if (c_month < 1) {
            tmp = "闰";
        }
        tmp += monString[c_month - 1];
        temp[2] = new String(tmp); // c_month
        tmp = "";
        tmp += ((cDay < 11) ? "初" : ((cDay < 20) ? "十" : ((cDay < 30) ? "廿" : "卅")));
        if (cDay % 10 != 0 || cDay == 10) {
            tmp += (numString[(cDay - 1) % 10]);
        }
        temp[3] = tmp; // c_day
        tmp = "";
        if (c_hour == 13) {
            tmp += ("夜");
        }
        tmp += dzString[(int) ((c_hour - 1) % 12)];
        temp[4] = new String(tmp);
        return temp;
    }

    protected int getBit(int m, int n) {
        return (m >> n) & 1;
    }

}