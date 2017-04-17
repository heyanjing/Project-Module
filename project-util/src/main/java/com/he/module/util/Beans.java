package com.he.module.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class Beans {

    public static <T> T convert2Bean(Object obj, Class<T> clazz) {
        return ObjectMappers.convert(obj, clazz);
    }

    public static <T> List<T> convert2List(Object objs, Class<T> clazz) {
        String json = ObjectMappers.toJson(objs);
        return ObjectMappers.toList(json, clazz);
    }

    public static Map<String, Object> convert2Map(Object bean) {
        return ObjectMappers.toMap(bean);
    }

    /**
     * 把source复制到target中
     */
    public static void copyProperties(Object bean, Object source) {
        try {
            BeanUtils.copyProperties(bean, source);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static void copyProperty(Object bean, String name, Object source) {
        try {
            BeanUtils.copyProperty(bean, name, source);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * 复制source中不能为空的对象
     */
    public static void copyNotNullProperty(Object target, Object source) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
            PropertyDescriptor[] pdList = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pdList) {
                Method writeMethod = pd.getWriteMethod();
                Method readMethod = pd.getReadMethod();
                if (readMethod == null || writeMethod == null) {
                    continue;
                }
                Object val = readMethod.invoke(source);
                if (val != null) {
                    writeMethod.invoke(target, val);
                }
            }
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    /**
     * Map中的属性复制到Bean
     */
    public static void populate(Object bean, Map<String, ? extends Object> properties) {
        try {
            BeanUtils.populate(bean, properties);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            BeanUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            throw Exceptions.newRuntimeException(e);
        }
    }
}
