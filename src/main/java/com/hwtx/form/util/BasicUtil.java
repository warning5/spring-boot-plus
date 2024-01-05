package com.hwtx.form.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class BasicUtil {

    public static final String SINGLE_CHAR = "abcdefghijklmnopqrstuvwxyz0123456789, .?'_-=+!@#$%^&*() ";

    /**
     * 是否为空或""或"null"(大写字母"NULL"不算空) 集合对象检查是否为空或集合中是否有对象
     *
     * @param obj       obj
     * @param recursion recursion
     *                  是否递归查检集合对象
     * @return boolean
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(boolean recursion, Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof Collection && recursion) {
            Collection collection = (Collection) obj;
            if (collection.isEmpty()) {
                return true;
            }
            for (Object item : collection) {
                if (!isEmpty(recursion, item)) {
                    return false;
                }
            }
        } else if (obj.getClass().isArray()) {
            if (obj instanceof int[]) {
                if (((int[]) obj).length > 0) {
                    return false;
                }
            } else if (obj instanceof double[]) {
                if (((double[]) obj).length > 0) {
                    return false;
                }
            } else if (obj instanceof float[]) {
                if (((float[]) obj).length > 0) {
                    return false;
                }
            } else if (obj instanceof short[]) {
                if (((short[]) obj).length > 0) {
                    return false;
                }
            } else if (obj instanceof byte[]) {
                if (((byte[]) obj).length > 0) {
                    return false;
                }
            } else if (obj instanceof boolean[]) {
                if (((boolean[]) obj).length > 0) {
                    return false;
                }
            } else {
                Object[] array = (Object[]) obj;
                for (Object item : array) {
                    if (!isEmpty(recursion, item)) {
                        return false;
                    }
                }
            }
        } else if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.isEmpty()) {
                return true;
            }
            if (recursion) {
                for (Object item : map.values()) {
                    if (!isEmpty(recursion, item)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            String tmp = obj.toString();
            if (null == tmp) {
                return true;
            }
            tmp = tmp.trim();
            if (!tmp.isEmpty() && !tmp.equals("null")) {
                return false;
            }
        }
        return true;
    }

    public static Long parseLong(Object value) throws Exception {
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Date) {
            Date date = (Date) value;
            return date.getTime();
        }
        if (value instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
            return timestamp.getTime();
        }
        if (value instanceof java.sql.Date) {
            Date date = (java.sql.Date) value;
            return date.getTime();
        }
        if (value instanceof LocalDateTime || value instanceof LocalDate) {
            return DateUtil.parse(value).getTime();
        }
        return Long.parseLong(value.toString());
    }

    public static Long parseLong(Object value, Long def) {
        try {
            return parseLong(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static BigDecimal parseDecimal(Object value, double def) {
        return parseDecimal(value, new BigDecimal(def));
    }

    public static BigDecimal parseDecimal(Object value, BigDecimal def) {
        if (null == value) {
            return def;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        BigDecimal result = null;
        if (value instanceof Long) {
            result = new BigDecimal((Long) value);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            result = new BigDecimal(date.getTime());
        } else if (value instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
            result = new BigDecimal(timestamp.getTime());
        } else if (value instanceof java.sql.Date) {
            Date date = (java.sql.Date) value;
            result = new BigDecimal(date.getTime());
        } else if (value instanceof LocalDateTime) {
            result = new BigDecimal(DateUtil.parse((LocalDateTime) value).getTime());
        } else if (value instanceof LocalDate) {
            result = new BigDecimal(DateUtil.parse((LocalDate) value).getTime());
        } else {
            try {
                result = new BigDecimal(value.toString());
            } catch (Exception e) {
                return def;
            }
        }
        return result;
    }

    public static boolean isEmpty(Object obj) {
        return isEmpty(false, obj);
    }

    /**
     * 是否全部为空
     *
     * @param objs objs
     * @return boolean
     */
    public static boolean isEmpty(Object... objs) {
        if (null == objs) {
            return true;
        }
        for (Object obj : objs) {
            if (!isEmpty(false, obj)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(false, obj);
    }

    public static boolean isNotEmpty(boolean recursion, Object obj) {
        return !isEmpty(recursion, obj);
    }

    public static boolean equals(byte[] bytes1, byte[] bytes2) {
        if (null == bytes1) {
            if (null == bytes2) {
                return true;
            } else {
                return false;
            }
        }
        if (null == bytes2) {
            return false;
        }
        int l1 = bytes1.length;
        int l2 = bytes2.length;
        if (l1 != l2) {
            return false;
        }
        for (int i = 0; i < l1; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(Object obj1, Object obj2) {
        if (null == obj1) {
            if (null == obj2) {
                return true;
            } else {
                return false;
            }
        } else {
            if (null == obj2) {
                return false;
            } else {
                return obj1.toString().equals(obj2.toString());
            }
        }
    }

    public static boolean equalsIgnoreCase(Object obj1, Object obj2) {
        if (null == obj1) {
            if (null == obj2) {
                return true;
            } else {
                return false;
            }
        } else {
            if (null == obj2) {
                return false;
            } else {
                return obj1.toString().equalsIgnoreCase(obj2.toString());
            }
        }
    }

    /**
     * nvl 取第一个不为null的值, 没有符合条件的 则返回null
     *
     * @param values values
     * @param <T>    T
     * @return T
     */
    public static <T> T nvl(T... values) {
        if (null == values) {
            return null;
        }
        for (T item : values) {
            if (null != item) {
                return item;
            }
        }
        return null;
    }


    /**
     * 反回第一个不为空(""|null|empty)的值 没有符合条件的 则返回NULL
     * 与nvl区别 : ""不符合evl条件 但符合nvl条件
     *
     * @param recursion recursion
     * @param values    values
     * @param <T>       T
     * @return Object
     */
    public static <T> T evl(boolean recursion, T... values) {
        if (null == values) {
            return null;
        }
        for (T item : values) {
            if (isNotEmpty(recursion, item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> T evl(T... values) {
        return evl(false, values);
    }

    /**
     * 生成随机字符串
     *
     * @param length length
     * @param buffer buffer
     * @return String
     */
    public static String getRandomString(int length, StringBuffer buffer) {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    public static String getRandomString(int length) {
        return getRandomString(length, new StringBuffer("012356789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    public static String getRandomNumberString(int length) {
        return getRandomString(length, new StringBuffer("123567890"));
    }

    /**
     * 在src的第idx位置插入key
     *
     * @param src src
     * @param idx idx
     * @param key key
     * @return String
     */
    public static String insert(String src, int idx, String key) {
        if (null == src || null == key) {
            return src;
        }
        src = src.substring(0, idx) + key + src.substring(idx);
        return src;

    }

    /**
     * 判断数字
     *
     * @param obj obj
     * @return boolean
     */
    public static boolean isNumber(Object obj) {
        boolean result = false;
        if (obj == null) {
            return result;
        }
        if (obj instanceof Number)
            return true;
        String str = obj.toString();
        try {
            Double.parseDouble(str);
            result = true;
        } catch (Exception e) {
        }
        return result;
    }

    public static Short parseShort(Object value, Short def) {
        if (null == value) {
            return def;
        }
        if (value instanceof Short) {
            return (Short) value;
        }
        try {
            return (short) Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static Short parseShort(Object value) throws Exception {
        return (short) Double.parseDouble(value.toString());
    }

    public static Integer parseInt(Object value, Integer def) {
        if (null == value) {
            return def;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return (int) Double.parseDouble(value.toString().trim());
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer parseInt(Object value) throws Exception {
        return (int) Double.parseDouble(value.toString());
    }


    public static Float parseFloat(Object value, Float def) {
        if (null == value) {
            return def;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        try {
            return Float.parseFloat(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    public static Double parseDouble(Object value, Double def) {
        if (null == value) {
            return def;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 类型转换
     *
     * @param obj obj
     * @param def def
     * @return Boolean
     */
    public static Boolean parseBoolean(Object obj, Boolean def) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        try {
            return parseBoolean(obj);
        } catch (Exception e) {
            return def;
        }
    }

    public static Boolean parseBoolean(Object obj) throws Exception {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if ("1".equals(obj.toString())
                || "true".equalsIgnoreCase(obj.toString())
                || "t".equalsIgnoreCase(obj.toString())
                || "on".equalsIgnoreCase(obj.toString())
                || "yes".equalsIgnoreCase(obj.toString())
                || "y".equalsIgnoreCase(obj.toString())) {
            return true;
        } else if ("0".equals(obj.toString())
                || "false".equalsIgnoreCase(obj.toString())
                || "off".equalsIgnoreCase(obj.toString())
                || "f".equalsIgnoreCase(obj.toString())
                || "no".equalsIgnoreCase(obj.toString())
                || "n".equalsIgnoreCase(obj.toString())) {
            return false;
        } else {
            return Boolean.parseBoolean(obj.toString());
        }
    }

    /**
     * 拆分权限数 : 将任意一个数拆分成多个（2的n次方）的和
     *
     * @param num num
     * @return List
     */
    public static List<String> parseLimit(int num) {
        List<String> list = new ArrayList<>();
        int count = 0;
        while (num >= 1) {
            int temp = num % 2;
            num = (num - temp) / 2;
            if (temp == 1) {
                if (count == 0) {
                    list.add("1");
                } else {
                    list.add((2 << (count - 1)) + "");
                }
            }
            count++;
        }
        return list;
    }

    /**
     * 字符串替换
     *
     * @param src     src
     * @param pattern pattern
     * @param replace replace
     * @return String
     */
    public static String replace(String src, String pattern, String replace) {
        if (src == null)
            return null;
        int s = 0;
        int e = 0;
        StringBuilder result = new StringBuilder();
        while ((e = src.indexOf(pattern, s)) >= 0) {
            result.append(src.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }

        result.append(src.substring(s));
        return result.toString();
    }

    /**
     * 删除空格
     *
     * @param str str
     * @return String
     */
    public static String trim(Object str) {
        String result = "";
        if (str != null) {
            if (!isNumber(str))
                result = str.toString().trim();
            else
                result = "" + str;
        } else {
            result = "";
        }
        if (result.equals("-1"))
            result = "";
        return result;
    }

    /**
     * 删除空格
     *
     * @param str str
     * @return String
     */
    public static String trim(String str) {
        String result = "";
        if (str != null) {
            if (!isNumber(str))
                result = str.toString().trim();
            else
                result = "" + str;
        } else {
            result = "";
        }
        if (result.equals("-1"))
            result = "";
        return result;
    }

    /**
     * 填充字符(从左侧填充)
     *
     * @param src 原文
     * @param chr 填充字符
     * @param len 需要达到的长度
     * @return String
     */
    public static String fillLChar(String src, String chr, int len) {
        if (null != src && null != chr && !chr.isEmpty()) {
            while (src.length() < len) {
                src = chr + src;
            }
        }
        return src;
    }

    public static String fillRChar(String src, String chr, int len) {
        if (null != src && null != chr && !chr.isEmpty()) {
            while (src.length() < len) {
                src = src + chr;
            }
        }
        return src;
    }

    public static String fillChar(String src, String chr, int len) {
        return fillLChar(src, chr, len);
    }

    public static String fillChar(String src, int len) {
        return fillChar(src, "0", len);
    }


    /**
     * 提取HashMap的key
     *
     * @param map map
     * @return List
     */
    public static List<String> getMapKeys(Map<?, ?> map) {
        List<String> keys = new ArrayList<>();
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            Object value = it.next();
            if (null != value) {
                keys.add(value.toString());
            }
        }
        return keys;
    }

    public static List<String> split(String str, String separator) {
        List<String> list = new ArrayList<>();
        if (null != str && null != separator) {
            String tmps[] = str.split(separator);
            for (String tmp : tmps) {
                tmp = tmp.trim();
                if (BasicUtil.isNotEmpty(tmp)) {
                    list.add(tmp);
                }
            }
        }
        return list;
    }


    /**
     * 子串出现次数
     *
     * @param src src
     * @param chr chr
     * @return int
     */
    public static int charCount(String src, String chr) {
        int count = 0;
        int idx = -1;
        if (null == src || null == chr || chr.trim().isEmpty()) {
            return 0;
        }
        while ((idx = src.indexOf(chr, idx + chr.length())) != -1) {
            count++;
        }
        return count;
    }

    /**
     * 截取子串
     *
     * @param src string
     * @param fr  开始位置
     * @param to  结束位置 负数表示倒数, 如-2表示删除最后2位
     * @return String
     */
    public static String cut(String src, int fr, int to) {
        if (null == src) {
            return null;
        }
        int len = src.length();
        if (to > len) {
            to = len;
        }
        if (to < 0) {
            to = src.length() + to;
        }
        if (to < 0 || to < fr) {
            return null;
        }
        return src.substring(fr, to);
    }

    /**
     * 从左侧开始取len位
     *
     * @param src String
     * @param len 截取长度
     * @return String
     */
    public static String left(String src, int len) {
        if (null == src) {
            return null;
        }
        int max = src.length();
        if (len > max) {
            len = max;
        }
        return src.substring(0, len);
    }

    /**
     * 从右侧开始取len位
     *
     * @param src String
     * @param len 截取长度
     * @return String
     */
    public static String right(String src, int len) {
        if (null == src) {
            return null;
        }
        int max = src.length();
        if (len > max) {
            len = max;
        }
        return src.substring(max - len, max);
    }

    /**
     * 超长部分忽略
     *
     * @param length 最长显示长度
     * @param src    原文
     * @return String
     */
    public static String ellipsis(int length, String src) {
        String result = "";
        int size = length * 2;
        String chrs[] = src.split("");
        long cnt = 0;
        boolean above = false;
        for (String chr : chrs) {
            if (cnt >= size) {
                above = true;
                break;
            }
            if (SINGLE_CHAR.contains(chr.toLowerCase())) {
                cnt += 1;
            } else {
                cnt += 2;
            }
            result += chr;
        }
        if (above) {
            result += "...";
        }
        return result;
    }

    /**
     * 获取本机IP
     *
     * @return List
     */
    @SuppressWarnings("rawtypes")
    public static List<InetAddress> localInetAddress() {
        List<InetAddress> ips = new ArrayList<>();
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip instanceof Inet4Address) {
                        ips.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return ips;
    }

    /**
     * 获取本机IP地址
     *
     * @return List
     */
    public static List<String> localIps() {
        List<String> ips = new ArrayList<>();
        List<InetAddress> list = localInetAddress();
        for (InetAddress ip : list) {
            ips.add(ip.getHostAddress());
        }
        return ips;
    }

    public static int index(boolean ignoreNull, Collection<Object> objs, Object obj) {
        return index(ignoreNull, objs, obj);
    }

    public static boolean contains(boolean ignoreNull, Collection<Object> objs, Object obj) {
        if (null == objs) {
            return false;
        }
        for (Object o : objs) {
            if (ignoreNull) {
                if (null == obj || null == o) {
                    continue;
                }
            } else {
                if (null == obj && null == o) {
                    return true;
                }
            }
            if (null == o) {
                continue;
            }
            assert obj != null;
            if (obj.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public static <T> int index(boolean ignoreNull, boolean ignoreCase, Collection<T> objs, String obj) {
        int idx = -1;
        if (null == objs) {
            return -1;
        }
        for (T o : objs) {
            idx++;
            if (ignoreNull) {
                if (null == obj || null == o) {
                    continue;
                }
            } else {
                if (null == obj && null == o) {
                    return idx;
                }
            }
            if (null != obj) {
                if (null == o) {
                    continue;
                }
                String val = o.toString();
                if (ignoreCase) {
                    obj = obj.toLowerCase();
                    val = val.toLowerCase();
                }
                if (obj.equals(val)) {
                    return idx;
                }
            }
        }
        return -1;
    }

    public static int index(Collection<Object> objs, String obj) {
        return index(false, false, objs, obj);
    }

    /**
     * 拼接字符
     *
     * @param list  list
     * @param split split
     * @return String
     */
    public static String concat(List<String> list, String split) {
        if (null == list) {
            return "";
        }
        StringBuffer result = new StringBuffer();
        for (String val : list) {
            if (BasicUtil.isEmpty(val)) {
                continue;
            }
            if (result.length() > 0) {
                result.append(split);
            }
            result.append(val);
        }
        return result.toString();
    }

    public static String concat(String split, String... values) {
        StringBuilder builder = new StringBuilder();
        if (null != values) {
            for (String value : values) {
                if (BasicUtil.isEmpty(value)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(value);
            }
        }
        return builder.toString();
    }

    public static String omit(String src, int left, int right, String ellipsis) {
        String result = "";
        if (BasicUtil.isEmpty(src)) {
            return result;
        }
        int length = src.length();
        if (left > length) {
            left = length;
        }
        if (right > length - left) {
            right = length - left;
        }
        String l = src.substring(0, left);
        String r = src.substring(length - right);
        result = l + BasicUtil.fillRChar("", ellipsis, length - left - right) + r;
        return result;
    }

    /**
     * 计算下标
     *
     * @param index 下标 从0开始 -1表示最后一行 -2表示倒数第2行
     * @param size  总行数
     * @return 最终下标
     */
    public static int index(int index, int size) {
        if (size == 0) {
            return 0;
        }
        if (index >= size) {
            index = size - 1;
        } else if (index < 0) {
            //倒数
            index = size + index;
            if (index < 0) {
                //超出0按0算
                index = 0;
            }
        }
        return index;
    }

    /**
     * 确认边界
     *
     * @param begin 开始
     * @param end   结束
     * @param qty   数量
     * @param total 总数
     * @return [开始, 结束]
     */
    public static int[] range(Integer begin, Integer end, Integer qty, Integer total) {
        int[] result = new int[2];
        if (null != begin && begin < 0) {
            begin = 0;
        }
        if (null != end && end < 0) {// end<0, 取最后-end个
            begin = total + end;
            end = total;
        }
        if (null != begin && null != qty) {
            end = begin + qty;
        }
        if (null != total) {
            if (null == end || end > total) {
                end = total;
            }
        }
        if (null == begin) {
            begin = 0;
        }
        if (null == end) {
            end = total;
        }
        if (end < begin) {
            end = begin;
        }
        if (begin < 0) {
            begin = 0;
        }
        if (end < 0) {
            end = 0;
        }
        result[0] = begin;
        result[1] = end;
        return result;
    }
}
