package com.he.module.util;

import java.util.List;
import java.util.Map;

public class Jsons {

    public static final String toJson(Object o) {
        return ObjectMappers.toJson(o);
    }

    public static final String toJson(Object o, boolean prettyFormat) {
        return ObjectMappers.toJson(o, null, prettyFormat);
    }

    public static final String toJson(Object o, String dateFormat) {
        return ObjectMappers.toJson(o, dateFormat, false);
    }

    public static final String toJson(Object o, String dateFormat, boolean prettyFormat) {
        return ObjectMappers.toJson(o, dateFormat, prettyFormat);
    }

    public static <T> T toBean(String jsonString, Class<T> clazz) {
        return ObjectMappers.toBean(jsonString, clazz);
    }

    public static List<Object> toList(String jsonString) {
        return ObjectMappers.toList(jsonString, Object.class);
    }

    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        return ObjectMappers.toList(jsonString, clazz);
    }

    public static Map<String, Object> toMap(String jsonString) {
        return ObjectMappers.toMap(jsonString);
    }

}
