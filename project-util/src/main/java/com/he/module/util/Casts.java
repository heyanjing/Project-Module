package com.he.module.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public abstract class Casts {

    /**
     * 转为 String 型
     */
    public static String toString(Object obj) {
        return toString(obj, "");
    }

    /**
     * 转为 String 型（提供默认值）
     */
    public static String toString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 转为 double 型
     */
    public static double toDouble(Object obj) {
        return toDouble(obj, 0);
    }

    /**
     * 转为 double 型（提供默认值）
     */
    public static double toDouble(Object obj, double defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null) {
            String strValue = toString(obj);
            if (Strings.isNotEmpty(strValue)) {
                doubleValue = Double.parseDouble(strValue);
            }
        }
        return doubleValue;
    }

    /**
     * 转为 long 型
     */
    public static long toLong(Object obj) {
        return toLong(obj, 0);
    }

    /**
     * 转为 long 型（提供默认值）
     */
    public static long toLong(Object obj, long defaultValue) {
        long longValue = defaultValue;
        if (obj != null) {
            String strValue = toString(obj);
            if (Strings.isNotEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    /**
     * 转为 int 型
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }

    /**
     * 转为 int 型（提供默认值）
     */
    public static int toInt(Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null) {
            String strValue = toString(obj);
            if (Strings.isNotEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    /**
     * 转为 boolean 型
     */
    public static boolean toBoolean(Object obj) {
        return toBoolean(obj, false);
    }

    /**
     * 转为 boolean 型（提供默认值）
     */
    public static boolean toBoolean(Object obj, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null) {
            booleanValue = Boolean.parseBoolean(toString(obj));
        }
        return booleanValue;
    }

    /**
     * 转为 String[] 型
     */
    public static String[] toStringArray(Object[] objArray) {
        if (objArray == null) {
            objArray = new Object[0];
        }
        String[] strArray = new String[objArray.length];
        if (ArrayUtils.isNotEmpty(objArray)) {
            for (int i = 0; i < objArray.length; i++) {
                strArray[i] = toString(objArray[i]);
            }
        }
        return strArray;
    }

    /**
     * 转为 double[] 型
     */
    public static double[] toDoubleArray(Object[] objArray) {
        if (objArray == null) {
            objArray = new Object[0];
        }
        double[] doubleArray = new double[objArray.length];
        if (!ArrayUtils.isEmpty(objArray)) {
            for (int i = 0; i < objArray.length; i++) {
                doubleArray[i] = toDouble(objArray[i]);
            }
        }
        return doubleArray;
    }

    /**
     * 转为 long[] 型
     */
    public static long[] toLongArray(Object[] objArray) {
        if (objArray == null) {
            objArray = new Object[0];
        }
        long[] longArray = new long[objArray.length];
        if (!ArrayUtils.isEmpty(objArray)) {
            for (int i = 0; i < objArray.length; i++) {
                longArray[i] = toLong(objArray[i]);
            }
        }
        return longArray;
    }

    /**
     * 转为 int[] 型
     */
    public static int[] toIntArray(Object[] objArray) {
        if (objArray == null) {
            objArray = new Object[0];
        }
        int[] intArray = new int[objArray.length];
        if (!ArrayUtils.isEmpty(objArray)) {
            for (int i = 0; i < objArray.length; i++) {
                intArray[i] = toInt(objArray[i]);
            }
        }
        return intArray;
    }

    /**
     * 转为 boolean[] 型
     */
    public static boolean[] toBooleanArray(Object[] objArray) {
        if (objArray == null) {
            objArray = new Object[0];
        }
        boolean[] booleanArray = new boolean[objArray.length];
        if (!ArrayUtils.isEmpty(objArray)) {
            for (int i = 0; i < objArray.length; i++) {
                booleanArray[i] = toBoolean(objArray[i]);
            }
        }
        return booleanArray;
    }

    /**
     * 转为 Byte[]
     */
    public static byte[] toBytes(Object obj) {
        String str = toString(obj);
        if ((str != null) && (str.length() % 2 == 0)) {
            byte[] bytes = new byte[str.length() / 2];
            for (int i = 0; i < str.length() / 2; i++) {
                String bytestr = str.substring(2 * i, 2 * i + 2);
                bytes[i] = (byte) Integer.parseInt(bytestr, 16);
            }
            return bytes;
        }
        throw new IllegalArgumentException("data is null or is not mutiple by 2");
    }

    /**
     * 将obj转为clazz
     */
    public static Object to(String obj, Class<?> clazz) {
        if (clazz.equals(Long.class)) {
            return Long.valueOf(obj);
        } else if (clazz.equals(Integer.class)) {
            return Integer.valueOf(obj);
        } else if (clazz.equals(Date.class)) {
            if (Strings.isNumeric(obj)) {
                return Dates.newDate(Long.valueOf(obj));
            } else {
                return Dates.newDate(obj);
            }
        } else {
            return clazz.cast(obj);
        }
    }

    public static <T> T toClass(String obj, Class<T> clazz) {
        return clazz.cast(obj);
    }

    public static <T> List<T> toList(Object objs, Class<T> clazz) {
        String jsonString = ObjectMappers.toJson(objs);
        return Jsons.toList(jsonString, clazz);
    }

}
