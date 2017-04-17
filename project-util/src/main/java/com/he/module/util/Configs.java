package com.he.module.util;

import org.apache.commons.configuration2.PropertiesConfiguration;

import com.he.module.Constants;

public class Configs {

    private static PropertiesConfiguration CONFIG = Configurations.newPropertiesConfiguration(Constants.STR_CONFIG_PROPERTIES);

    /**
     * 获取 String 类型的属性值
     */
    public static String getString(String key) {
        return CONFIG.getString(key);
    }

    /**
     * 获取 String 类型的属性值（可指定默认值）
     */
    public static String getString(String key, String defaultValue) {
        return CONFIG.getString(key, defaultValue);
    }

    public static boolean getBoolean(String key) {
        return CONFIG.getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return CONFIG.getBoolean(key, defaultValue);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return CONFIG.getBoolean(key, defaultValue);
    }

    public static Integer getInteger(String key) {
        return CONFIG.getInt(key);
    }

    public static Integer getInteger(String key, Integer defaultValue) {
        return CONFIG.getInteger(key, defaultValue);
    }

}
