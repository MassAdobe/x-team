package com.x.team.common.utils;

import com.x.team.common.constants.CommonConstants;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：工具类
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:47
 */
@SuppressWarnings("all")
public class CommonUtils {

    private final static String RANDOM_SEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final static String RANDOM_NUM = "1234567890";
    // 手机号正则
    private final static String MOBILE_REGEX = "^1[0-9]{10}$";
    private final static String DATE_YYYY_MM_DD = "yyyy-MM-dd";
    private final static String DATE_YYYY_MM_DD_CHINESE = "yyyy年MM月dd日";
    private final static String DATE_YYYY_MM = "yyyy-MM";
    private final static String DATE_YYYYMM = "yyyyMM";
    private final static String DATE_YYYY_MM_DD_HMS = "yyyy-MM-dd HH:mm:ss";
    private final static String DATEYYYYMMDDHMS = "yyyyMMddHHmmss";
    private final static String HMS = "HH:mm:ss";
    private final static String HM = "HH:mm";
    private final static String DATE_YYYY_MM_DD_HM = "yyyy-MM-dd HH:mm";
    private final static String DEFAULT_EXPIRE_DT = "1970-01-01";
    private final static String SHANGHAI_TIME_ZONE = "Asia/Shanghai";
    // 身份证正则
    private final static String ID_CARD_NUM_REG = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
            "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
    // 前十七位加权因子
    private final static int[] ID_CARD_NUM_WI = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    // 这是除以11后，可能产生的11位余数对应的验证码
    private final static String[] ID_CARD_NUM_Y = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    // 正则判断只能由字母、数字、空格符、横线及下划线组成
    public final static String IOT_NAME_REG = "[\\s0-9a-zA-Z_/.-]*";

    /**
     * 描述：生成 a->b A->B 0->9 的入参位数随机字符串
     *
     * @author 月关
     * @date Created in 2021/8/19 11:00 上午
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(RANDOM_SEED.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 描述：生成 a->b A->B 0->9 的入参位数随机字符串
     *
     * @author 月关
     * @date Created in 2021/8/19 11:01 上午
     */
    public static String getRandomNum(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(RANDOM_NUM.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 描述：返回两个数之间的随机数
     *
     * @param start 开始数值
     * @param end   结束数值
     * @return 随机数
     * @author 月关
     * @date Created in 2022/3/1 4:27 下午
     */
    public static int getRandomNumBetween(int start, int end) {
        return (int) (Math.random() * (end - start) + start);
    }

    /**
     * 描述：校验手机号
     *
     * @author 月关
     * @date Created in 2021/8/19 11:01 上午
     */
    public static boolean isMobile(String mobile) {
        if (Strings.isNullOrEmpty(mobile)) {
            return false;
        }
        Pattern p = Pattern.compile(MOBILE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 描述：校验时间是否过期以及时间是否为'1970-01-01'，如果过期返回true，如果没有过期或者为'1970-01-01'则返回false
     *
     * @author 月关
     * @date Created in 2021/8/19 11:01 上午
     */
    public static boolean checkExpireDt(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYY_MM_DD);
        return !new Date().before(date) && !sdf.format(date).equals(DEFAULT_EXPIRE_DT);
    }

    /**
     * 描述：通过Date返回yyyy-MM-dd HH:mm:ss的string时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:02 上午
     */
    public static String rtnTm(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYY_MM_DD_HMS);
        return sdf.format(date);
    }

    /**
     * 描述：通过Date返回yyyyMM的string时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:02 上午
     */
    public static String rtnM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYYMM);
        return sdf.format(date);
    }

    /**
     * 描述：通过Date返回yyyy-MM-dd HH:mm的string时间
     *
     * @param date
     * @return
     */
    public static String rtnTmM(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYY_MM_DD_HM);
        return sdf.format(date);
    }

    /**
     * 描述：通过Date返回yyyy-MM-dd HH:mm:ss的string时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:02 上午
     */
    public static String rtnTmWithoutFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEYYYYMMDDHMS);
        return sdf.format(date);
    }

    /**
     * 描述：通过Date返回yyyy-MM-dd HH:mm:ss的string时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:02 上午
     */
    public static String rtnTmNoSpace(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEYYYYMMDDHMS);
        return sdf.format(date);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param startTime 时间参数 1 格式：1990-01-01 12:00:00
     * @param endTime   时间参数 2 格式：2009-01-01 12:00:00
     * @return long 秒
     */
    public static long getDistanceTimes(String startTime, Date endTime) {
        DateFormat df = new SimpleDateFormat(DATEYYYYMMDDHMS);
        Date one;
        long diff = 0;
        try {
            one = df.parse(startTime);
            long time1 = one.getTime() / 1000;
            long time2 = endTime.getTime() / 1000;

            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    /**
     * 描述：通过Date返回yyyy-MM-dd HH:mm的string时间
     *
     * @author 月关
     * @date Created in 2021/8/31 3:02 下午
     */
    public static String rtnTmWithoutSecond(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(HM);
        return sdf.format(date);
    }

    /**
     * 描述：通过Date返回yyyy-MM-dd的string日期
     *
     * @author 月关
     * @date Created in 2021/8/19 10:58 上午
     */
    public static String rtnDt(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYY_MM_DD);
        return sdf.format(date);
    }

    /**
     * 描述：通过时间String返回Time时间 返回格式 10:15
     *
     * @author 月关
     * @date Created in 2021/9/28
     */
    public static String rtnHMTm(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(HM);
        return sdf.format(date);
    }

    /**
     * 描述：转换YYYYMMDDHHMMSS 2 YYYY-MM-DD HH:MM:SS
     *
     * @author 月关
     * @date Created in 2021/9/28
     */
    public static String formatYYYYMMDDHHMMSS(String date) {
        return date.substring(0, 4) + CommonConstants.HORIZONTAL_MARK + date.substring(4, 6) + CommonConstants.HORIZONTAL_MARK + date.substring(6, 8) + " " + date.substring(8, 10) + CommonConstants.COLON_MARK + date.substring(10, 12) + CommonConstants.COLON_MARK + date.substring(12);
    }

    /**
     * 描述：校验过期时间，如果时间为1970-01-01，则返回最大时间，其他则返回正常时间
     *
     * @author 月关
     * @date Created in 2021/8/19 10:58 上午
     */
    public static String rtnExpireDt(Date date) {
        String expDt = rtnDt(date);
        // 说明没有过期时间
        if (DEFAULT_EXPIRE_DT.equals(expDt)) {
            expDt = "9999-12-31";
        }
        return expDt;
    }

    /**
     * 描述：是否是错误的身份证号码
     *
     * @author 月关
     * @date Created in 2021/8/19 11:02 上午
     */
    public static boolean isIdCardNum(String idCardNum) {
        if (Strings.isNullOrEmpty(idCardNum)) {
            return false;
        }
        boolean matches = idCardNum.matches(ID_CARD_NUM_REG);
        // 判断第18位校验值
        if (matches) {
            if (idCardNum.length() == 18) {
                try {
                    char[] charArray = idCardNum.toCharArray();
                    int sum = 0;
                    for (int i = 0; i < ID_CARD_NUM_WI.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * ID_CARD_NUM_WI[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    return ID_CARD_NUM_Y[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase());
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return matches;
    }

    /**
     * 描述：通过日期String返回Date日期
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static Date rtnDateChinese(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_YYYY_MM_DD_CHINESE);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 描述：通过日期String(2021-09)返回Date日期
     *
     * @author 月关
     * @date Created in 2021/10/13 11:03 晚上
     */
    public static Date rtnMonthDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_YYYY_MM);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 描述：通过时间String返回Date时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static Date rtnDateTm(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_YYYY_MM_DD_HMS);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 描述：通过时间String返回Date时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static Date rtnDateTmM(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_YYYY_MM_DD_HM);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 描述：通过时间String返回Time时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static Date rtnTm(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat(HMS);
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 描述：通过时间String返回Time时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static Date rtnDate(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_YYYY_MM_DD);
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 描述：比较有效和删除，如果有效未删除返回true；如果有效删除，无效未删除，无效删除返回false
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static boolean compareIsEnabledDeleted(Integer isEnabled, Integer isDeleted) {
        if (null != isDeleted && 1 == isDeleted) {
            return null != isEnabled && 1 == isEnabled;
        }
        return false;
    }

    /**
     * 描述：如果firsts存在于seconds，则返回存在的List
     *
     * @author 月关
     * @date Created in 2021/8/19 11:03 上午
     */
    public static List<Long> rtnExistsList(List<Long> firsts, List<Long> seconds) {
        List<Long> rtnLists = new ArrayList<>();
        firsts.forEach(id -> {
            if (seconds.contains(id)) {
                rtnLists.add(id);
            }
        });
        return rtnLists;
    }

    /**
     * 描述：返回当前年份
     *
     * @author 月关
     * @date Created in 2021/8/19 11:04 上午
     */
    public static String rtnCurYear() {
        String date = rtnDt(new Date());
        return date.substring(0, 4);
    }

    /**
     * 描述：获取当前月份
     *
     * @author 月关
     * @date Created in 2021/8/19 11:04 上午
     */
    public static Integer rtnCurMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 描述：切割过长的In搜索
     *
     * @author 月关
     * @date Created in 2021/8/31 3:33 下午
     */
    public static <T> List<List<T>> getSumArrayList(List<T> list) {
        List<List<T>> objects = new ArrayList<>();
        int iSize = list.size() / 500;
        int iCount = list.size() % 500;
        for (int i = 0; i <= iSize; i++) {
            List<T> newObjList = new ArrayList<>();
            if (i == iSize) {
                for (int j = i * 500; j < i * 500 + iCount; j++) {
                    newObjList.add(list.get(j));
                }
            } else {
                for (int j = i * 500; j < (i + 1) * 500; j++) {
                    newObjList.add(list.get(j));
                }
            }
            if (newObjList.size() > 0) {
                objects.add(newObjList);
            }
        }
        return objects;
    }

    /**
     * 描述：通过身份证计算年龄
     *
     * @author 月关
     * @date Created in 2021/9/2 4:14 下午
     */
    public static Integer evaluateAgeByIdCardNum(String idCardNum) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        int year = Integer.parseInt(idCardNum.substring(6, 10));
        int month = Integer.parseInt(idCardNum.substring(10, 12));
        int day = Integer.parseInt(idCardNum.substring(12, 14));
        if ((month < monthNow) || (month == monthNow && day <= dayNow)) {
            return yearNow - year;
        } else {
            return yearNow - year - 1;
        }
    }

    /**
     * 描述：生成设备类型编号
     *
     * @author 月关
     * @date Created in 2021/9/7 1:05 下午
     */
    public static String generateDeviceTypeCode(int deviceId) {
        if (10 > deviceId) {
            return "00" + deviceId;
        } else if (100 > deviceId) {
            return "0" + deviceId;
        } else {
            return String.valueOf(deviceId);
        }
    }

    /**
     * 判断str1中包含str2的个数
     *
     * @param str1
     * @param str2
     * @return counter
     * @author 月关
     * @date Created in 2021/9/7 1:05 下午
     */
    public static int countStr(String str1, String str2) {
        if (str1 == null || str2 == null || "".equals(str1.trim()) || "".equals(str2.trim())) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = str1.indexOf(str2, index)) != -1) {
            index = index + str2.length();
            count++;
        }
        return count;
    }

    /**
     * 描述：现在至第二天凌晨差多少秒
     *
     * @author 月关
     * @date Created in 2022/1/22 4:36 下午
     */
    public static Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 根据年月获取对应月份天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDayCountByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;

    }

    /**
     * 获取输入时间的后minCount分钟的时间
     *
     * @param date
     * @param minCount
     * @return
     */
    public static Date getNextMinTime(Date date, int minCount) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.MINUTE, ca.get(Calendar.MINUTE) + minCount);
        return ca.getTime();
    }

    /**
     * 工作单元参数保留两位小数
     *
     * @param value
     * @return
     */
    public static BigDecimal paramFormat(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd;
    }

    /**
     * 描述：通过Date返回yyyy-MM的string时间
     *
     * @author 月关
     * @date Created in 2021/8/19 11:02 上午
     */
    public static String rtnMStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_YYYY_MM);
        return sdf.format(date);
    }

    /**
     * 获取指定月份的所有日期,如果是当前月份，到当天
     *
     * @param month 指定月份(yyyy-MM)
     * @return
     */
    public static List<String> getMonthDay(String month) {
        List<String> result = new ArrayList<>();
        Date now = new Date();
        int fromDate = 1;
        int toDate = 1;
        if (month.equals(rtnMStr(now))) {
            // 当月
            Calendar ca = Calendar.getInstance();
            ca.setTime(now);
            toDate = ca.get(Calendar.DAY_OF_MONTH);
            for (int i = toDate; i >= fromDate; --i) {
                ca.set(Calendar.DAY_OF_MONTH, i);
                String date = rtnDt(ca.getTime());
                result.add(date);
            }
        } else {
            // 历史月
            Date monthDate = rtnMonthDate(month);
            Calendar ca = Calendar.getInstance();
            ca.setTime(monthDate);
            toDate = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = toDate; i >= fromDate; --i) {
                ca.set(Calendar.DAY_OF_MONTH, i);
                String date = rtnDt(ca.getTime());
                result.add(date);
            }
        }
        return result;
    }

    public static String zonedDateTimeToString(ZonedDateTime zonedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_YYYY_MM_DD_HMS)
                .withZone(ZoneId.of(SHANGHAI_TIME_ZONE));
        if (zonedDateTime != null) {
            return zonedDateTime.format(formatter);
        }
        return CommonConstants.EMPTY;
    }

    /**
     * 获取当前日期的前一天日期
     *
     * @param date 当前日期
     * @return 前一天日期
     */
    public static String rtnYesterday(Date date) {
        // 格式转换
        SimpleDateFormat fm = new SimpleDateFormat(DATE_YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        // 设置当前时间
        calendar.setTime(date);
        // 减去一天
        calendar.add(Calendar.DATE, -1);
        return fm.format(calendar.getTime());
    }
}
