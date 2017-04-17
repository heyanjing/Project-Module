package com.he.module.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.he.module.Constants;

/**
 * 提供json与obj之间的转换
 */
public class ObjectMappers {

    private static ObjectMapper objectMapper = null;

    // ########################################
    // ###***********创建ObjectMapper**************####
    // ########################################
    public static ObjectMapper newObjectMapper(String dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); // 单引号
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);// 未引号字段
        if (dateFormat == null) {
            dateFormat = Constants.DATETIME_DEFAULT;
            mapper.setDateFormat(new SimpleDateFormat(dateFormat));
        }
        return mapper;
    }

    public static ObjectMapper getInstance(String dateFormat) {
        if (objectMapper == null) {
            objectMapper = newObjectMapper(dateFormat);
        }
        return objectMapper;
    }

    public static ObjectMapper getInstance() {
        return getInstance(null);
    }

    // ########################################
    // ###*************convert**************####
    // ########################################
    public static <T> T convert(Object obj, Class<T> clazz, TypeReference<?> type) {
        ObjectMapper mapper = getInstance();
        if (type != null) {
            return mapper.convertValue(obj, type);
        } else {
            return mapper.convertValue(obj, clazz);
        }
    }

    public static <T> T convert(Object obj, Class<T> clazz) {
        return convert(obj, clazz, null);
    }

    public static <T> T convert(Object obj, TypeReference<?> type) {
        return convert(obj, null, type);
    }

    // ########################################
    // ###**************toJson**************####
    // ########################################

    public static String toJson(Object o, String dateFormat, boolean prettyFormat) {
        String json = null;
        ObjectMapper mapper = getInstance(dateFormat);
        try {
            if (prettyFormat) {
                json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            } else {
                json = mapper.writeValueAsString(o);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
        return json;
    }

    public static String toJson(Object o) {
        return toJson(o, null, false);
    }

    public static String toJson(Object o, boolean prettyFormat) {
        return toJson(o, null, prettyFormat);
    }

    // ########################################
    // ###**************toBean**************####
    // ########################################
    public static <T> T toBean(String json, Class<T> clazz, TypeReference<?> type) {
        ObjectMapper mapper = getInstance();
        try {
            if (type != null) {
                return mapper.readValue(json, type);
            } else {
                return mapper.readValue(json, clazz);
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static <T> T toBean(String json, Class<T> clazz) {
        return ObjectMappers.toBean(json, clazz, null);
    }

    public static <T> T toBean(String json, TypeReference<?> type) {
        return ObjectMappers.toBean(json, null, type);
    }

    // ########################################
    // ###*************toList**************####
    // ########################################
    public static <T> List<T> toList(String json, Class<T> clazz) {
        List<T> objs = Lists.newArrayList();
        List<LinkedHashMap<String, Object>> maps = toBean(json, new TypeReference<List<Object>>() {});
        if (maps != null) {
            for (LinkedHashMap<String, Object> map : maps) {
                objs.add(convert(map, clazz));
            }
        }
        return objs;
    }

    // ########################################
    // ###*************toMap**************####
    // ########################################
    public static Map<String, Object> toMap(Object obj) {
        return convert(obj, new TypeReference<HashMap<String, Object>>() {});
    }
}
