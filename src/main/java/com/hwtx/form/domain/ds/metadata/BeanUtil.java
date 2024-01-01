package com.hwtx.form.domain.ds.metadata;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.hwtx.form.domain.ds.BasicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BeanUtil {
    public static ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);

    static {
        DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

        JSON_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // Include.Include.ALWAYS 默认
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
        // Include.NON_NULL 属性为NULL 不序列化

        JSON_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSON_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JSON_MAPPER.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
        JSON_MAPPER.registerModule(javaTimeModule);
        JSON_MAPPER.setTimeZone(TimeZone.getDefault());

    }


    private static ObjectMapper newObjectMapper(JsonInclude.Include include) {
        ObjectMapper result = new ObjectMapper();
        result.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        result.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        result.setSerializationInclusion(include);
        return result;
    }

    public static Object puarseFieldValue(Object value) {
        Object v = value;
        return v;
    }

    public static Double[] double2Double(double[] array) {
        if (null == array) {
            return null;
        }
        Double[] result = new Double[array.length];
        int idx = 0;
        for (double item : array) {
            result[idx++] = item;
        }
        return result;
    }

    public static double[] Double2double(Double[] array, double def) {
        if (null == array) {
            return null;
        }
        double[] result = new double[array.length];
        int idx = 0;
        for (Double item : array) {
            if (null == item) {
                item = def;
            }
            result[idx++] = item;
        }
        return result;
    }

    public static Long[] long2Long(long[] array) {
        if (null == array) {
            return null;
        }
        Long[] result = new Long[array.length];
        int idx = 0;
        for (long item : array) {
            result[idx++] = item;
        }
        return result;
    }

    public static long[] Long2long(Long[] array, long def) {
        if (null == array) {
            return null;
        }
        long[] result = new long[array.length];
        int idx = 0;
        for (Long item : array) {
            if (null == item) {
                item = def;
            }
            result[idx++] = item;
        }
        return result;
    }

    public static Integer[] int2Integer(int[] array) {
        if (null == array) {
            return null;
        }
        Integer[] result = new Integer[array.length];
        int idx = 0;
        for (int item : array) {
            result[idx++] = item;
        }
        return result;
    }

    public static int[] Integer2int(Integer[] array, int def) {
        if (null == array) {
            return null;
        }
        int[] result = new int[array.length];
        int idx = 0;
        for (Integer item : array) {
            if (null == item) {
                item = def;
            }
            result[idx++] = item;
        }
        return result;
    }

    public static Float[] float2Float(float[] array) {
        if (null == array) {
            return null;
        }
        Float[] result = new Float[array.length];
        int idx = 0;
        for (float item : array) {
            result[idx++] = item;
        }
        return result;
    }

    public static float[] Float2float(Float[] array, float def) {
        if (null == array) {
            return null;
        }
        float[] result = new float[array.length];
        int idx = 0;
        for (Float item : array) {
            if (null == item) {
                item = def;
            }
            result[idx++] = item;
        }
        return result;
    }

    public static byte[] char2bytes(char[] chars) {
        Charset charset = Charset.forName("ISO-8859-1");
        CharBuffer charBuffer = CharBuffer.allocate(chars.length);
        charBuffer.put(chars);
        charBuffer.flip();
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        return byteBuffer.array();
    }

    public static char[] byte2char(byte[] bytes) {
        Charset charset = Charset.forName("ISO-8859-1");
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        CharBuffer charBuffer = charset.decode(byteBuffer);
        return charBuffer.array();
    }

    /**
     * 设置属性值
     *
     * @param obj       对象
     * @param field     属性名
     * @param value     值
     * @param recursion 是递归查换父类属性
     * @param alert     设备失败是否提示日期
     * @return boolean
     */
    public static boolean setFieldValue(Object obj, String field, Object value, boolean recursion, boolean alert) {
        if (null == obj || null == field) {
            return false;
        }
        if (obj instanceof Map) {
            Map tmp = (Map) obj;
            tmp.put(field, value);
        } else {
            Field f = ClassUtil.getField(obj.getClass(), field, recursion);
            setFieldValue(obj, f, value);
        }
        return true;
    }

    public static void setFieldValue(Object object, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            log.error(String.format("无法设置字段值，field = %s,object = %s, value = %s", field, object, value), e);
            throw new RuntimeException("设置数据失败");
        }
    }


    public static Object getFieldValue(Object obj, Field field) {
        Object value = null;
        if (null == obj || null == field) {
            return null;
        }
        try {
            if (field.isAccessible()) {
                // 可访问属性
                value = field.get(obj);
            } else {
                // 不可访问属性
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        } catch (Exception e) {
            return null;
        }
        return value;
    }

    @SuppressWarnings("rawtypes")
    public static List<String> getMapKeys(Map map) {
        List<String> list = new ArrayList<>();
        for (Object key : map.keySet()) {
            list.add(key.toString());
        }
        return list;
    }

    /**
     * 按key升序拼接
     *
     * @param map         数据源
     * @param join        key, value之间的拼接符(默认=)
     * @param separator   separator 多个kv的分隔符(默认&amp;)
     * @param ignoreEmpty 是否忽略空值
     * @param order       是否排序
     * @return String(a = 1 & amp ; b = 2 & amp ; b = 3)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String map2string(Map map, String join, String separator, boolean ignoreEmpty, boolean order) {
        StringBuilder result = new StringBuilder();
        Set es = null;
        if (order) {
            SortedMap<String, Object> wrap = new TreeMap<String, Object>(map);
            es = wrap.entrySet();
        } else {
            es = map.entrySet();
        }
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (ignoreEmpty && BasicUtil.isEmpty(v)) {
                continue;
            }
            if (v instanceof Collection) {
                List list = new ArrayList();
                list.addAll((Collection) v);
                Collections.sort(list);
                for (Object item : list) {
                    if (ignoreEmpty && BasicUtil.isEmpty(item)) {
                        continue;
                    }
                    if (result.length() > 0) {
                        result.append(separator);
                    }
                    result.append(k).append(join).append(item);
                }
            } else if (v instanceof String[]) {
                String vals[] = (String[]) v;
                Arrays.sort(vals);
                for (String item : vals) {
                    if (ignoreEmpty && BasicUtil.isEmpty(item)) {
                        continue;
                    }
                    if (result.length() > 0) {
                        result.append(separator);
                    }
                    result.append(k).append(join).append(item);
                }
            } else {
                if (result.length() > 0) {
                    result.append(separator);
                }
                result.append(k).append(join).append(v);
            }
        }
        return result.toString();
    }

    public static String object2json(Object obj, JsonInclude.Include include) {
        if (null != obj) {
            //json类型直接返回
            if (obj.getClass().getName().toUpperCase().contains("JSON")) {
                return obj.toString();
            }
            try {
                if (null != include) {
                    return newObjectMapper(include).writeValueAsString(obj);
                }
                return JSON_MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String object2json(Object obj) {
        return object2json(obj, null);
    }

    /**
     * 参数转map
     * 参数格式a=1&amp;b=2&amp;b=3
     * 如果是多个值, 以String的List形式保存
     * 如果是url将根据问号分割
     *
     * @param url     参数或url
     * @param empty   结果中是否包含空值, 所有空值以""形式保存
     * @param decode  是否需要解码
     * @param charset 解码编码
     * @return Map
     */
    public static Map<String, Object> param2map(String url, boolean empty, boolean decode, String charset) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (null != url) {
            int index = url.indexOf("?");
            if (index != -1) {
                url = url.substring(index);
            }
            String[] kvs = url.split("&");
            for (String kv : kvs) {
                String k = null;
                String v = null;

                int idx = kv.indexOf("=");
                if (idx != -1) {
                    k = kv.substring(0, idx);
                    v = kv.substring(idx + 1);
                }
                if ("null".equals(v)) {
                    v = "";
                } else if ("NULL".equals(v)) {
                    v = null;
                }
                if (BasicUtil.isEmpty(v) && !empty) {
                    continue;
                }
                if (decode) {
                    v = urlDecode(v, charset);
                }
                if (params.containsKey(k)) {
                    Object olds = params.get(k);
                    List<String> vs = new ArrayList<>();
                    if (null == olds) {
                        vs.add(null);
                    } else if (olds instanceof String) {
                        vs.add(olds.toString());
                    } else if (olds instanceof ArrayList) {
                        vs = (ArrayList) olds;
                    }
                    vs.add(v);
                    params.put(k, vs);
                } else {
                    params.put(k, v);
                }

            }
        }
        return params;
    }

    public static String urlDecode(String src, String charset) {
        String result = null;
        if (null != src) {
            try {
                if (null == charset) {
                    result = URLDecoder.decode(src);
                } else {
                    result = URLDecoder.decode(src, charset);
                }
            } catch (Exception e) {
                result = src;
            }
        }
        return result;
    }

    /**
     * 集合拼接
     *
     * @param list     list
     * @param split    分隔符
     * @param required 是否必须(遇到宿舍是否忽略)
     * @return String
     */
    public static String concat(Collection<?> list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Collection<?> list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Collection<?> list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Collection<?> list) {
        return concat(list, false);
    }


    public static <T> String concat(T[] list, String key, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static <T> String concat(T[] list, String key, String split) {
        return concat(list, key, split, false);
    }

    public static <T> String concat(T[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static <T> String concat(T[] list, String split) {
        return concat(list, split, false);
    }

    public static <T> String concat(T[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static <T> String concat(T[] list) {
        return concat(list, false);
    }


    public static String concat(Integer[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Integer[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Integer[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Integer[] list) {
        return concat(list, false);
    }

    public static String concat(Long[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Long[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Long[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Long[] list) {
        return concat(list, false);
    }

    public static String concat(Double[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Double[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Double[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Double[] list) {
        return concat(list, false);
    }

    public static String concat(Float[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Float[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Float[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Float[] list) {
        return concat(list, false);
    }

    public static String concat(Short[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Short[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Short[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Short[] list) {
        return concat(list, false);
    }

    public static String concat(Byte[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(Byte[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(Byte[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(Byte[] list) {
        return concat(list, false);
    }


    public static String concat(int[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(int[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(int[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(int[] list) {
        return concat(list, false);
    }

    public static String concat(long[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(long[] list, String split) {
        return concat(list, split);
    }

    public static String concat(long[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(long[] list) {
        return concat(list, false);
    }

    public static String concat(double[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(double[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(double[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(double[] list) {
        return concat(list, false);
    }

    public static String concat(float[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(float[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(float[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(float[] list) {
        return concat(list, false);
    }

    public static String concat(short[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(short[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(short[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(short[] list) {
        return concat(list, false);
    }

    public static String concat(byte[] list, String split, boolean required) {
        StringBuilder builder = new StringBuilder();
        if (null != list) {
            for (Object item : list) {
                if (!required && BasicUtil.isEmpty(item)) {
                    continue;
                }
                if (builder.length() > 0) {
                    builder.append(split);
                }
                builder.append(item);
            }
        }
        return builder.toString();
    }

    public static String concat(byte[] list, String split) {
        return concat(list, split, false);
    }

    public static String concat(byte[] list, boolean required) {
        return concat(list, ",", required);
    }

    public static String concat(byte[] list) {
        return concat(list, false);
    }

    public static List wrap(Collection list, String wrap) {
        List result = new ArrayList<>();
        for (Object obj : list) {
            if (null == obj) {
                result.add(null);
            } else {
                if (BasicUtil.isNumber(obj)) {
                    result.add(obj);
                } else {
                    result.add(wrap + obj + wrap);
                }
            }
        }
        return result;
    }

    public static List<String> toUpperCase(List<String> list) {
        return toUpperCase(list, false);
    }

    /**
     * 条目转换大写
     *
     * @param list   list
     * @param update 是否更新原集合 或创建新集合
     * @return List
     */
    public static List<String> toUpperCase(List<String> list, boolean update) {
        if (null == list) {
            return list;
        }
        List<String> result = null;
        if (!update) {
            result = new ArrayList<>();
            for (String value : list) {
                if (null != value) {
                    value = value.toUpperCase();
                }
                result.add(value);
            }
        } else {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                String value = list.get(i);
                if (null != value) {
                    result.set(i, value.toUpperCase());
                }
            }
            result = list;
        }

        return result;
    }

    public static List<String> toLowerCase(List<String> list) {
        if (null != list) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                String value = list.get(i);
                if (null != value) {
                    list.set(i, value.toLowerCase());
                }
            }
        }
        return list;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object toUpperCaseKey(Object obj, String... keys) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Date) {
            return obj;
        }
        if (obj instanceof Map) {
            obj = toUpperCaseKey((Map<String, Object>) obj, keys);
        } else if (obj instanceof Collection) {
            obj = toUpperCaseKey((Collection) obj, keys);
        }
        return obj;
    }

    @SuppressWarnings("rawtypes")
    public static Collection toUpperCaseKey(Collection con, String... keys) {
        if (null == con) {
            return con;
        }
        for (Object obj : con) {
            obj = toUpperCaseKey(obj, keys);
        }
        return con;
    }

    /**
     * 需要保证数据类型一致
     *
     * @param list list
     * @param <T>  T
     * @return array
     */
    public static <T> T[] list2array(List<T> list) {
        if (null == list || list.isEmpty()) {
            return null;
        }
        T[] result = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        int index = 0;
        for (T item : list) {
            result[index++] = item;
        }
        return result;
    }

    /**
     * 与toString不同的是 中间没有空格与引号[1, 2, 3]而不是[1, 2, 3]
     *
     * @param list List
     * @return String
     */
    public static String list2string(List<?> list) {
        return "[" + concat(list) + "]";
    }

    public static <T> String array2string(T[] array) {
        return "[" + concat(array) + "]";
    }

    /**
     * 数组转list
     *
     * @param value 数组或集合
     * @param <T>
     * @return List
     */
    public static <T> List<T> list(Object value) {
        List<T> list = new ArrayList<>();
        if (null != value) {
            if (value instanceof Collection) {
                Collection<T> collection = (Collection<T>) value;
                for (T item : collection) {
                    list.add(item);
                }
            } else if (value.getClass().isArray()) {
                if (value instanceof int[]) {
                    int[] array = (int[]) value;
                    for (Integer item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof long[]) {
                    long[] array = (long[]) value;
                    for (Long item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof double[]) {
                    double[] array = (double[]) value;
                    for (Double item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof float[]) {
                    float[] array = (float[]) value;
                    for (Float item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof short[]) {
                    short[] array = (short[]) value;
                    for (Short item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof byte[]) {
                    byte[] array = (byte[]) value;
                    for (Byte item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof char[]) {
                    char[] array = (char[]) value;
                    for (Character item : array) {
                        list.add((T) item);
                    }
                } else if (value instanceof boolean[]) {
                    boolean[] array = (boolean[]) value;
                    for (Boolean item : array) {
                        list.add((T) item);
                    }
                } else {
                    Object[] array = (Object[]) value;
                    for (Object item : array) {
                        list.add((T) item);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 截取数组
     *
     * @param array 原数组
     * @param fr    开始位置
     * @param to    结束位置
     * @param <T>   数据类型
     * @return 新数组
     */
    public static <T> T[] cut(T[] array, int fr, int to) {
        if (null == array || array.length == 0) {
            return array;
        }
        T[] result = (T[]) Array.newInstance(array[0].getClass(), to - fr + 1);
        for (int i = fr; i <= to; i++) {
            result[i - fr] = array[i];
        }
        return result;
    }

    public static byte[] cut(byte[] array, int fr, int to) {
        if (null == array || array.length == 0) {
            return array;
        }
        byte[] result = new byte[to - fr + 1];
        for (int i = fr; i <= to; i++) {
            result[i - fr] = array[i];
        }
        return result;
    }

    public static short[] cut(short[] array, int fr, int to) {
        if (null == array || array.length == 0) {
            return array;
        }
        short[] result = new short[to - fr + 1];
        for (int i = fr; i <= to; i++) {
            result[i - fr] = array[i];
        }
        return result;
    }

    public static int[] cut(int[] array, int fr, int to) {
        if (null == array || array.length == 0) {
            return array;
        }
        int[] result = new int[to - fr + 1];
        for (int i = fr; i <= to; i++) {
            result[i - fr] = array[i];
        }
        return result;
    }

    /**
     * 左补齐
     *
     * @param bytes bytes
     * @param len   len
     * @return bytes
     */
    public static byte[] fill(byte[] bytes, int len) {
        byte[] result = new byte[len];
        for (int i = 0; i < bytes.length && i < len; i++) {
            result[len + i - bytes.length] = bytes[i];
        }
        return result;
    }

    /**
     * 删除空值
     *
     * @param map       map
     * @param recursion 是否递归检测集合map类型值的长度
     */
    public static void clearEmpty(Map<String, Object> map, boolean recursion) {
        if (null == map) {
            return;
        }
        List<String> keys = BasicUtil.getMapKeys(map);
        for (String key : keys) {
            Object value = map.get(key);
            if (BasicUtil.isEmpty(recursion, value)) {
                map.remove(key);
            }
        }
    }

    public static void clearEmpty(Map<String, Object> map) {
        clearEmpty(map, true);
    }

    /**
     * 删除空值
     *
     * @param list      list
     * @param recursion 是否递归检测集合map类型值的长度
     */
    public static void clearEmpty(List<Object> list, boolean recursion) {
        if (null == list) {
            return;
        }
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            if (BasicUtil.isEmpty(recursion, list.get(i))) {
                list.remove(i);
            }
        }
    }

    public static void clearEmpty(List<Object> list) {
        clearEmpty(list, true);
    }

    /**
     * 多个数组合并成一个数组(二维数组合成一维数组)
     *
     * @param <T>   T
     * @param first first
     * @param rest  rest
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] union(T[] first, T[]... rest) {
        int len = first.length;
        for (T[] array : rest) {
            len += array.length;
        }
        T[] result = Arrays.copyOf(first, len);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }


    /**
     * 集合中与value差值最小的成员的下标
     *
     * @param array array
     * @param value value
     * @return int
     */
    public static int closest(short[] array, short value) {
        int index = 0;
        int dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            int abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(Short[] array, short value) {
        int index = 0;
        int dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            int abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(List<Short> array, short value) {
        int index = 0;
        int dif = -1;
        int len = array.size();
        for (int i = 0; i < len; i++) {
            if (array.get(i) == value) {
                index = i;
                break;
            }
            int abs = Math.abs(array.get(i) - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(int[] array, int value) {
        int index = 0;
        int dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            int abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(Integer[] array, int value) {
        int index = 0;
        int dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            int abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(List<Integer> array, int value) {
        int index = 0;
        int dif = -1;
        int len = array.size();
        for (int i = 0; i < len; i++) {
            if (array.get(i) == value) {
                index = i;
                break;
            }
            int abs = Math.abs(array.get(i) - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(long[] array, long value) {
        int index = 0;
        long dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            long abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(Long[] array, long value) {
        int index = 0;
        long dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            long abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(List<Long> array, long value) {
        int index = 0;
        long dif = -1;
        int len = array.size();
        for (int i = 0; i < len; i++) {
            if (array.get(i) == value) {
                index = i;
                break;
            }
            long abs = Math.abs(array.get(i) - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(float[] array, float value) {
        int index = 0;
        float dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            float abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(Float[] array, float value) {
        int index = 0;
        float dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            float abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }

        }
        return index;
    }

    public static int closest(List<Float> array, float value) {
        int index = 0;
        float dif = -1;
        int len = array.size();
        for (int i = 0; i < len; i++) {
            if (array.get(i) == value) {
                index = i;
                break;
            }
            float abs = Math.abs(array.get(i) - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(double[] array, double value) {
        int index = 0;
        double dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            double abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(Double[] array, double value) {
        int index = 0;
        double dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            if (array[i] == value) {
                index = i;
                break;
            }
            double abs = Math.abs(array[i] - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }

        }
        return index;
    }

    public static int closest(List<Double> array, double value) {
        int index = 0;
        double dif = -1;
        int len = array.size();
        for (int i = 0; i < len; i++) {
            if (array.get(i) == value) {
                index = i;
                break;
            }
            double abs = Math.abs(array.get(i) - value);
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(BigDecimal[] array, BigDecimal value) {
        int index = 0;
        double dif = -1;
        int len = array.length;
        for (int i = 0; i < len; i++) {
            double abs = Math.abs(array[i].subtract(value).doubleValue());
            if (abs == 0) {
                index = i;
                break;
            }
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    public static int closest(List<BigDecimal> array, BigDecimal value) {
        int index = 0;
        double dif = -1;
        int len = array.size();
        for (int i = 0; i < len; i++) {
            double abs = Math.abs(array.get(i).subtract(value).doubleValue());
            if (abs == 0) {
                index = i;
                break;
            }
            if (dif == -1 || dif > abs) {
                dif = abs;
                index = i;
            }
        }
        return index;
    }

    /**
     * 集合截取
     *
     * @param <T>   t
     * @param list  list
     * @param begin begin
     * @param end   end
     * @return List
     */
    public static <T> List<T> cuts(Collection<T> list, int begin, int end) {
        List<T> result = new ArrayList<T>();
        if (null != list) {
            if (begin <= 0) {
                begin = 0;
            }
            if (end < 0 || end >= list.size()) {
                end = list.size() - 1;
            }
        }
        int idx = 0;
        for (T obj : list) {
            if (idx >= begin && idx <= end) {
                result.add(obj);
            }
            idx++;
            if (idx > end) {
                break;
            }
        }
        return result;
    }

    /**
     * 驼峰转下划线
     * userName : user_name
     *
     * @param str src
     * @return String
     */
    public static String camel_(String str) {
        if (null == str || str.contains("_")) {
            return str;
        }
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String g = matcher.group();
            matcher.appendReplacement(sb, "_" + g.toLowerCase());
        }
        matcher.appendTail(sb);
        if (sb.charAt(0) == '_') {
            sb.delete(0, 1);
        }
        return sb.toString();
    }

    /**
     * 转驼峰
     *
     * @param key  src
     * @param hold 是否保留分隔符
     * @return String
     */
    public static String camel(String key, boolean hold) {
        if (!key.contains("-") && !key.contains("_")) {
            return key;
        }
        String[] ks = key.split("_|-");
        String sKey = null;
        for (String k : ks) {
            if (null == sKey) {
                sKey = k.toLowerCase();
            } else {
                if (hold) {
                    sKey += "_";
                }
                sKey += CharUtil.toUpperCaseHeader(k.toLowerCase());
            }
        }
        return sKey;
    }


    public static String camel(String key) {
        return camel(key, false);
    }

    public static String Camel(String key, boolean hold) {
        if (!key.contains("-") && !key.contains("_")) {
            return key;
        }
        String[] ks = key.split("_|-");
        String sKey = null;
        for (String k : ks) {
            if (null == sKey) {
                sKey = CharUtil.toUpperCaseHeader(k.toLowerCase());
            } else {
                if (hold) {
                    sKey += "_";
                }
                sKey += CharUtil.toUpperCaseHeader(k.toLowerCase());
            }
        }
        return sKey;
    }

    public static String Camel(String key) {
        return Camel(key, false);
    }

    /**
     * 解析 key:vlue形式参数age:20
     * 返回数组["age","20"]
     * 如果值为空返回["age",""]
     * 如果没有分隔符返回["age","age"]
     *
     * @param src src
     * @return String
     */
    public static String[] parseKeyValue(String src) {
        if (BasicUtil.isEmpty(src)) {
            return null;
        }
        int len = 2;
        String[] result = null;
        String key1 = src;
        String key2 = src;
        if (src.contains(":")) {
            String tmp[] = src.split(":");
            len = NumberUtil.max(len, tmp.length);
            result = new String[len];
            key1 = tmp[0];
            if (tmp.length > 1) {
                key2 = tmp[1];
            } else {
                key2 = "";
            }
            for (int i = 2; i < len; i++) {
                result[i] = tmp[i];
            }
        } else {
            result = new String[2];
        }
        result[0] = key1;
        result[1] = key2;
        return result;
    }

    public static boolean isJson(Object json) {
        if (null == json) {
            return false;
        }
        try {
            JSON_MAPPER.readTree(json.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Object value(JsonNode json) {
        if (null == json) {
            return null;
        } else {
            if (json.isNull()) {
                return null;
            } else if (json.isInt()) {
                return json.asInt();
            } else if (json.isBoolean()) {
                return json.asBoolean();
            } else if (json.isDouble()) {
                return json.asDouble();
            } else if (json.isLong()) {
                return json.asLong();
            } else {
                return json.asText();
            }
        }
    }

    /**
     * 提取第一个不为空的value
     *
     * @param map  map
     * @param keys keys
     * @return String
     */
    public static Object propertyNvl(Map<String, ?> map, String... keys) {
        Object value = null;
        if (null == map || null == keys) {
            return value;
        }
        for (String key : keys) {
            value = map.get(key);
            if (null != value) {
                return value;
            }
            // 以下划线分隔的key
            String[] ks = key.split("_");
            String sKey = null;
            for (String k : ks) {
                if (null == sKey) {
                    sKey = k;
                } else {
                    sKey = sKey + CharUtil.toUpperCaseHeader(k);
                }
            }
            value = map.get(sKey);
            if (null != value) {
                return value;
            }
            // 以中划线分隔的key
            ks = key.split("-");
            sKey = null;
            for (String k : ks) {
                if (null == sKey) {
                    sKey = k;
                } else {
                    sKey = sKey + CharUtil.toUpperCaseHeader(k);
                }
            }
            value = map.get(sKey);
            if (null != value) {
                return value;
            }
        }
        return value;
    }

    /**
     * 设置所有属性值
     *
     * @param obj   obj
     * @param map   map
     * @param alert 赋值失败时是否提示异常信息
     */
    public static void setFieldsValue(Object obj, Map<String, ?> map, boolean alert) {
        if (null != map && null != obj) {
            List<String> fields = ClassUtil.getFieldsName(obj.getClass());
            for (String field : fields) {
                Object value = propertyNvl(map, field);
                if (BasicUtil.isNotEmpty(value)) {
                    setFieldValue(obj, field, value, true, alert);
                }
            }
        }
    }

    public static void setFieldsValue(Object obj, Map<String, ?> map) {
        setFieldsValue(obj, map, true);
    }

    public static byte[] serialize(Object value) {
        byte[] rv = new byte[0];
        if (value == null) {
            return rv;
        }
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
                if (bos != null) bos.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return rv;
    }

    public static Object deserialize(byte[] in) {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
                is.close();
                bis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (bis != null) bis.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return rv;
    }

    public static <T> Collection array2collection(Object array) {
        Collection<T> list = new ArrayList<>();
        return list;
    }

    public static <T> List<T> array2list(T[]... arrays) {
        List<T> list = new ArrayList<T>();
        if (null != arrays) {
            for (T[] array : arrays) {
                if (null != array) {
                    //list.addAll(Arrays.asList(array));
                    for (T item : array) {
                        if (item instanceof Collection) {
                            list.addAll((Collection) item);
                        } else {
                            list.add(item);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static List object2list(Object obj) {
        List list = null;
        if (null == obj) {
            return new ArrayList();
        }
        if (obj instanceof List) {
            return (List) obj;
        }
        list = new ArrayList();
        if (obj instanceof Object[]) {
            Object[] objs = (Object[]) obj;
            for (Object item : objs) {
                list.add(item);
            }
        } else if (obj instanceof Collection) {
            list.addAll((Collection) obj);
        }
        return list;
    }

    /**
     * 合成笛卡尔组合
     *
     * @param lists 二维数组
     * @param <T>   t
     * @return List
     * 输入:
     * [[A, B, C], [1, 2, 3]]
     * 输出:
     * [[A, 1], [A, 2], [A, 3]
     * , [B, 1], [B, 2], [B, 3]
     * , [C, 1], [C, 2], [C, 3]]
     * <p>
     * 输入:
     * [[A, B, C], [1, 2, 3], [一, 二, 三]]
     * 输出:
     * [[A, 1, 一], [A, 1, 二], [A, 1, 三], [A, 2, 一], [A, 2, 二], [A, 2, 三], [A, 3, 一], [A, 3, 二], [A, 3, 三]
     * , [B, 1, 一], [B, 1, 二], [B, 1, 三], [B, 2, 一], [B, 2, 二], [B, 2, 三], [B, 3, 一], [B, 3, 二], [B, 3, 三]
     * , [C, 1, 一], [C, 1, 二], [C, 1, 三], [C, 2, 一], [C, 2, 二], [C, 2, 三], [C, 3, 一], [C, 3, 二], [C, 3, 三]
     * ]
     */
    public static <T> List<List<T>> descartes(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<List<T>>();
        if (null == lists || lists.size() == 0) {
            return result;
        }
        List<T> st = lists.get(0);
        for (T t : st) {
            List<T> tmp = new ArrayList<T>();
            tmp.add(t);
            result.add(tmp);
        }
        List<List<T>> store = new ArrayList<List<T>>();
        for (int i = 1; i < lists.size(); i++) {
            List<T> r2 = lists.get(i);
            for (int j = 0; j < result.size(); j++) {
                List<T> rns = result.get(j);
                for (int k = 0; k < r2.size(); k++) {
                    List<T> mid = new ArrayList<T>();
                    mid.addAll(rns);
                    mid.add(r2.get(k));
                    store.add(mid);
                }
            }
            result = new ArrayList<List<T>>();
            result.addAll(store);
            store = new ArrayList<List<T>>();
        }
        return result;
    }

    public static <T> List<List<T>> descartes(List<T>... lists) {
        return descartes(array2list(lists));
    }

    /**
     * 合并成新数组
     *
     * @param array 第一个数组
     * @param items 其他数组
     * @param <T>   数据类型
     * @return 合并后数组
     */
    public static <T> T[] merge(T[] array, T[]... items) {
        T[] result = null;
        int len = array.length;
        Class clazz = null;
        if (null != items) {
            for (T[] item : items) {
                if (null != item) {
                    len += array.length;
                    if (null == array && null == clazz) {
                        for (T obj : item) {
                            if (null != obj) {
                                clazz = obj.getClass();
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (null != array) {
            result = Arrays.copyOf(array, len);
        } else {
            if (null != clazz) {
                result = (T[]) Array.newInstance(clazz, len);
            } else {
                return null;
            }
        }


        int offset = array.length;
        if (null != items) {
            for (T[] item : items) {
                if (null != item) {
                    System.arraycopy(item, 0, result, offset, item.length);
                    offset += item.length;
                }
            }
        }
        return result;
    }


    /**
     * maps合并成新map
     *
     * @param maps map
     * @param <K>  k
     * @param <V>  v
     * @return map
     */
    public static <K, V> Map<K, V> merge(Map<K, V>... maps) {
        Map<K, V> result = new HashMap<>();
        if (null != maps) {
            for (Map<K, V> map : maps) {
                join(result, map, true);
            }
        }
        return result;
    }

    /**
     * copy合并成src中
     *
     * @param src  src
     * @param copy copy6
     * @param over key相同时是否覆盖
     * @param <K>  k
     * @param <V>  v
     * @return map
     */
    public static <K, V> Map<K, V> join(Map<K, V> src, Map<K, V> copy, boolean over) {
        if (null == src) {
            src = new HashMap<K, V>();
        }
        if (null != copy) {
            for (K key : copy.keySet()) {
                if (!over && src.containsKey(key)) {
                    continue;
                }
                src.put(key, copy.get(key));
            }
        }
        return src;
    }


    /**
     * list与items合并成新集合
     *
     * @param list  list
     * @param items items
     * @param <T>   T
     * @return list
     */
    public static <T> List<T> merge(Collection<T> list, T... items) {
        List<T> result = new ArrayList<>();
        if (null != list) {
            result.addAll(list);
        }
        if (null != items) {
            for (T item : items) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<T> merge(List<T> list, T... items) {
        List<T> result = new ArrayList<>();
        if (null != list) {
            result.addAll(list);
        }
        if (null != items) {
            for (T item : items) {
                result.add(item);
            }
        }
        return result;
    }


    /**
     * items拼接到list中
     *
     * @param list  list
     * @param items items
     * @param <T>   T
     * @return list
     */
    public static <T> List<T> join(List<T> list, T... items) {
        if (null == list) {
            list = new ArrayList<>();
        }
        if (null != items) {
            for (T item : items) {
                list.add(item);
            }
        }
        return list;
    }

    public static <T> Collection<T> join(Collection<T> list, T... items) {
        if (null == list) {
            list = new ArrayList<>();
        }
        if (null != items) {
            for (T item : items) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 添加集合,并去重(不区分大小写)
     *
     * @param list     list
     * @param appends  appends
     * @param distinct 去重
     */
    public static <T> void join(boolean distinct, Collection<T> list, Collection<T> appends) {
        for (T append : appends) {
            if (!distinct || !contains(list, append)) {
                list.add(append);
            }
        }
    }

    public static <T> void join(boolean distinct, Collection<T> list, T... appends) {
        for (T append : appends) {
            if (!distinct || !contains(list, append)) {
                list.add(append);
            }
        }
    }

    /**
     * list中是否包含item 不区分大小写
     *
     * @param list list
     * @param item item
     * @return boolean
     */
    public static <T> boolean contains(Collection<T> list, Object item) {
        for (T i : list) {
            if (null == i && null == item) {
                return true;
            }
            if (null != i && i.toString().equalsIgnoreCase(item.toString())) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, Object> copy(Map<String, Object> into, Map<String, Object> copy, List<String> keys) {
        if (null == copy) {
            return into;
        }
        if (null != keys) {
            for (String key : keys) {
                String ks[] = parseKeyValue(key);
                into.put(ks[0], copy.get(ks[1]));
            }
        }
        return into;
    }

    public static Map<String, Object> copy(Map<String, Object> into, Map<String, Object> copy, String... keys) {
        if (null == copy) {
            return into;
        }
        if (null != keys) {
            for (String key : keys) {
                String ks[] = parseKeyValue(key);
                into.put(ks[0], copy.get(ks[1]));
            }
        }
        return into;
    }

    public static Map<String, Object> copy(Map<String, Object> into, Map<String, Object> copy) {
        return copy(into, copy, getMapKeys(copy));
    }

    private static String concatValue(Map<String, Object> row, String split) {
        StringBuilder builder = new StringBuilder();
        List<String> keys = getMapKeys(row);
        for (String key : keys) {
            if (builder.length() > 0) {
                builder.append(split);
            }
            builder.append(row.get(key));
        }
        return builder.toString();
    }

    public static <T> List<Map<String, Object>> pivot(Collection<T> datas, List<String> pks, List<String> classKeys, String... valueKeys) {
        List<String> list = new ArrayList<>();
        if (null != valueKeys) {
            for (String item : valueKeys) {
                list.add(item);
            }
        }
        return pivot(datas, pks, classKeys, valueKeys);
    }

    private static String[] kvs(Map<String, Object> row) {
        List<String> keys = getMapKeys(row);
        int size = keys.size();
        String[] kvs = new String[size * 2];
        for (int i = 0; i < size; i++) {
            String k = keys.get(i);
            String value = null;
            Object v = row.get(k);
            if (null != v) {
                value = v.toString();
            }
            kvs[i * 2] = k;
            kvs[i * 2 + 1] = value;
        }
        return kvs;
    }

    /**
     * distinct 不区分大小写
     *
     * @param list List
     * @return List
     */
    public static List<String> distinct(Collection<String> list) {
        List<String> result = new ArrayList<>();
        List<String> check = new ArrayList<>();
        if (null != list) {
            for (String item : list) {
                String upper = item.toUpperCase();
                if (!check.contains(upper)) {
                    result.add(item);
                    check.add(upper);
                }
            }
        }
        return result;
    }

    /**
     * removeAll 不区分大小写
     *
     * @param src    src
     * @param remove remove
     * @return List
     */
    public static List<String> removeAll(List<String> src, List<String> remove) {
        List<String> check = new ArrayList<>();
        if (null != src) {
            remove = toUpperCase(remove);
            for (String item : src) {
                if (null != item && remove.contains(item.toUpperCase())) {
                    check.add(item);
                }
            }
            src.removeAll(check);
        }
        return src;
    }


    public static List<String> removeAll(List<String> src, String... remove) {
        return removeAll(src, array2list(remove));
    }

    public static <T> List<T> copy(Collection<T> list) {
        return merge(list);
    }

    public static <T> Collection<T> copy(Collection<T> tar, Collection<T>... items) {
        if (null != tar && null != items) {
            for (Collection<T> item : items) {
                tar.addAll(item);
            }
        }
        return tar;
    }

    /**
     * 复制copy的属性值到to
     *
     * @param to   赋值给to
     * @param copy copy
     */
    public static void copyFieldValue(Object to, Object copy) {
        List<Field> fields = ClassUtil.getFields(to.getClass(), false, false);
        for (Field field : fields) {
            setFieldValue(to, field, getFieldValue(copy, field));
        }
    }


    /**
     * 根据配置文件提取指定key的值
     * @param prefix 前缀
     * @param env 配置文件环境
     * @param keys key列表 第一个有值的key生效
     * @return String
     */

    /**
     * 根据配置文件提取指定key的值
     *
     * @param prefix 前缀
     * @param env    配置文件环境
     * @param keys   key列表 第一个有值的key生效
     * @return String
     */
    public static String value(String prefix, Environment env, String... keys) {
        String value = null;
        if (null == env || null == keys) {
            return value;
        }
        if (null == prefix) {
            prefix = "";
        }
        for (String key : keys) {
            key = prefix + key;
            value = env.getProperty(key);
            if (null != value) {
                return value;
            }
            //以中划线分隔的配置文件
            String[] ks = key.split("-");
            String sKey = null;
            for (String k : ks) {
                if (null == sKey) {
                    sKey = k;
                } else {
                    sKey = sKey + CharUtil.toUpperCaseHeader(k);
                }
            }
            value = env.getProperty(sKey);
            if (null != value) {
                return value;
            }

            //以下划线分隔的配置文件
            ks = key.split("_");
            sKey = null;
            for (String k : ks) {
                if (null == sKey) {
                    sKey = k;
                } else {
                    sKey = sKey + CharUtil.toUpperCaseHeader(k);
                }
            }
            value = env.getProperty(sKey);
            if (null != value) {
                return value;
            }

            ks = key.toLowerCase().split("_");
            sKey = null;
            for (String k : ks) {
                if (null == sKey) {
                    sKey = k;
                } else {
                    sKey = sKey + CharUtil.toUpperCaseHeader(k);
                }
            }
            value = env.getProperty(sKey);
            if (null != value) {
                return value;
            }

            //中划线
            sKey = key.replace("_", "-");
            value = env.getProperty(sKey);
            if (null != value) {
                return value;
            }

            //小写中划线
            sKey = key.toLowerCase().replace("_", "-");
            value = env.getProperty(sKey);
            if (null != value) {
                return value;
            }
        }
        return value;
    }
}
