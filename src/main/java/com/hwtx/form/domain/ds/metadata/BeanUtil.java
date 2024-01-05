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
import com.hwtx.form.util.BasicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
                log.error("", e);
            }
        }
        return null;
    }

    public static String object2json(Object obj) {
        return object2json(obj, null);
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
            for (List<T> rns : result) {
                for (T t : r2) {
                    List<T> mid = new ArrayList<T>();
                    mid.addAll(rns);
                    mid.add(t);
                    store.add(mid);
                }
            }
            result = new ArrayList<>();
            result.addAll(store);
            store = new ArrayList<>();
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
