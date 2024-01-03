package com.hwtx.form.domain.ds.metadata;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

@Slf4j
public class BasicUtil {

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
                return ((int[]) obj).length <= 0;
            } else if (obj instanceof double[]) {
                return ((double[]) obj).length <= 0;
            } else if (obj instanceof float[]) {
                return ((float[]) obj).length <= 0;
            } else if (obj instanceof short[]) {
                return ((short[]) obj).length <= 0;
            } else if (obj instanceof byte[]) {
                return ((byte[]) obj).length <= 0;
            } else if (obj instanceof boolean[]) {
                return ((boolean[]) obj).length <= 0;
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
            return tmp.isEmpty() || tmp.equals("null");
        }
        return true;
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
            return null == bytes2;
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
            return null == obj2;
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
            return null == obj2;
        } else {
            if (null == obj2) {
                return false;
            } else {
                return obj1.toString().equalsIgnoreCase(obj2.toString());
            }
        }
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
            result = false;
        }
        return result;
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

    /**
     * 数组是否包含
     *
     * @param objs       objs
     * @param obj        obj
     * @param ignoreCase 是否不区分大小写
     * @param ignoreNull 是否忽略null, 如果忽略 则无论是否包含null都返回false
     * @return boolean
     */
    public static boolean containsString(boolean ignoreNull, boolean ignoreCase, Object[] objs, String obj) {
        if (null == objs) {
            return false;
        }
        return containsString(ignoreNull, ignoreCase, BeanUtil.array2list(objs), obj);
    }

    public static int index(boolean ignoreNull, boolean ignoreCase, Object[] objs, String obj) {
        if (null == objs) {
            return -1;
        }
        return index(ignoreNull, ignoreCase, BeanUtil.array2list(objs), obj);
    }

    public static boolean containsString(Object[] objs, String obj) {
        return containsString(false, false, objs, obj);
    }

    public static int index(Object[] objs, String obj) {
        return index(false, false, objs, obj);
    }

    public static boolean contains(Object[] objs, Object obj) {
        if (null == objs) {
            return false;
        }
        return contains(false, BeanUtil.array2list(objs), obj);
    }

    public static int index(Object[] objs, Object obj) {
        if (null == objs) {
            return -1;
        }
        return index(false, BeanUtil.array2list(objs), obj);
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

    public static <T> boolean containsString(boolean ignoreNull, boolean ignoreCase, Collection<T> objs, String obj) {
        if (null == objs) {
            return false;
        }
        int idx = 0;
        for (T o : objs) {
            if (ignoreNull) {
                if (null == obj || null == o) {
                    continue;
                }
            } else {
                if (null == obj && null == o) {
                    return true;
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
                    return true;
                }
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
        StringBuilder result = new StringBuilder();
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
}
