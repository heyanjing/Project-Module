package com.he.module.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 生成随机数据
 * TODO Randoms.getObject() -> Randoms.newObject();
 * 
 * @author Yrain
 */
public class Randoms {

    public static final String  NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String  NUMBERS             = "0123456789";
    public static final String  LETTERS             = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String  CAPITAL_LETTERS     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String  LOWER_CASE_LETTERS  = "abcdefghijklmnopqrstuvwxyz";

    private static final Random r                   = new Random(System.currentTimeMillis());

    private static final String prefix              = "-";

    public static int getInt() {
        return getInt(Integer.MAX_VALUE);
    }

    public static int getInt(int startInclusive, int endExclusive) {
        return RandomUtils.nextInt(startInclusive, endExclusive);
    }

    public static int getInt(int n) {
        return RandomUtils.nextInt(0, n);
    }

    public static long getLong() {
        return r.nextLong();
    }

    public static long getLong(int startInclusive, int endExclusive) {
        return RandomUtils.nextLong(startInclusive, endExclusive);
    }

    public static long getLong(int startInclusive, Long endExclusive) {
        return RandomUtils.nextLong(startInclusive, endExclusive);
    }

    public static long getLong(int endExclusive) {
        return RandomUtils.nextLong(0, endExclusive);
    }

    public static double getDouble() {
        return r.nextDouble();
    }

    public static float getFloat() {
        return r.nextFloat();
    }

    public static Boolean getBoolean() {
        return r.nextInt(10) > 5;
    }

    /**
     * 返回随机名称, prefix字符串+5位随机数字.
     */
    public static String getStringWith(String str) {
        return getStringWith(str, Integer.MAX_VALUE);
    }

    /**
     * 返回随机名称, prefix字符串+5位随机数字.
     */
    public static String getStringWith(String str, int n) {
        return str + prefix + r.nextInt(n);
    }

    /**
     * 生成指定长度的字母和数字的随机组合字符串
     */
    public static String getString(int n) {
        return RandomStringUtils.randomAlphanumeric(n);
    }

    /**
     * 使用指定的字符生成n位长度的随机字符串
     */
    public static String getStringIn(int n, String chars) {
        return getStringIn(n, chars.toCharArray());
    }

    /**
     * 使用指定的字符生成n位长度的随机字符串
     */
    public static String getStringIn(int n, char[] chars) {
        return RandomStringUtils.random(n, chars);
    }

    /**
     * 生成随机数字字符串
     */
    public static String getStringNumeric(int n) {
        return RandomStringUtils.randomNumeric(n);
    }

    /**
     * 生成String数组
     * n:个数
     * l:每一个的长度
     */
    public static String[] getStringArray(int n, int l) {
        String[] datas = new String[n];
        for (int i = 0; i < n; i++) {
            datas[i] = getString(l);
        }
        return datas;
    }

    /**
     * 从输入list中随机返回随机个对象.
     */
    public static List<?> getList(List<?> list) {
        return getList(list, getInt(list.size()));
    }

    /**
     * 从输入list中随机返回n个对象.
     */
    public static <T> List<T> getList(List<T> list, int n) {
        Collections.shuffle(list);
        return n > 0 && n < list.size() ? list.subList(0, n) : list;
    }

    /**
     * 从输入list中随机返回一个对象.
     */
    public static <T> T getListOne(List<T> list) {
        List<T> shuffle_list = getList(list, 1);
        return shuffle_list.size() > 0 ? shuffle_list.get(0) : null;
    }

    public static <T> T getObject(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            if (!ArrayUtils.isEmpty(fields)) {
                for (Field f : fields) {
                    if (!isStatic(f)) {
                        setRandomValue(t, f.getName(), f.getType());
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw Exceptions.newRuntimeException(e);
        }
        return t;
    }

    public static <T> List<T> getObject(Class<T> clazz, int n) {
        List<T> objs = Lists.newArrayList();
        for (int i = 0; i < n; i++) {
            objs.add(Randoms.getObject(clazz));
        }
        return objs;
    }

    public static <T> List<T> getObjects(Class<T> clazz, int n) {
        return getObject(clazz, n);
    }

    /**
     * 判断字段是否为static
     */
    public static boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    private static void setRandomValue(Object obj, String fieldName, Class<?> fieldType) {
        if (fieldType.equals(String.class)) {
            Reflections.setFieldValue(obj, fieldName, Randoms.getStringWith(fieldName));
        } else if (fieldType.equals(Long.class)) {
            Reflections.setFieldValue(obj, fieldName, Randoms.getLong(1, 10000));
        } else if (fieldType.equals(Short.class)) {
            Reflections.setFieldValue(obj, fieldName, (short) Randoms.getInt(1, 100));
        } else if (fieldType.equals(Integer.class)) {
            Reflections.setFieldValue(obj, fieldName, Randoms.getInt(1, 10000));
        } else if (fieldType.equals(Double.class)) {
            Reflections.setFieldValue(obj, fieldName, Randoms.getDouble());
        } else if (fieldType.equals(Float.class)) {
            Reflections.setFieldValue(obj, fieldName, Randoms.getFloat());
        } else if (fieldType.equals(Boolean.class)) {
            Reflections.setFieldValue(obj, fieldName, Randoms.getBoolean());
        } else if (fieldType.equals(Date.class)) {
            Reflections.setFieldValue(obj, fieldName, Dates.newDate());
        } else if (fieldType.equals(Timestamp.class)) {
            Reflections.setFieldValue(obj, fieldName, new Timestamp(System.currentTimeMillis()));
        } else if (fieldType.equals(java.sql.Date.class)) {
            Reflections.setFieldValue(obj, fieldName, new java.sql.Date(System.currentTimeMillis()));
        } else if (fieldType.equals(List.class)) {
            Reflections.setFieldValue(obj, fieldName, Lists.newArrayList());
        } else if (fieldType.equals(Map.class)) {
            Reflections.setFieldValue(obj, fieldName, Maps.newHashMap());
        } else if (fieldType.equals(Object[].class)) {
            Reflections.setFieldValue(obj, fieldName, Lists.newArrayList().toArray());
        } else if (fieldType.equals(Object.class)) {
            Reflections.setFieldValue(obj, fieldName, null);
        }
    }
}
