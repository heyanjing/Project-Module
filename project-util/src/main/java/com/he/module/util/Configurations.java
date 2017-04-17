package com.he.module.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.slf4j.Logger;

/**
 * 加载属性文件
 */
public class Configurations {

    private static final Logger LOGGER = Logs.getLogger(Configurations.class);

    public static PropertiesConfiguration newPropertiesConfiguration(Object obj) {

        org.apache.commons.configuration2.builder.fluent.Configurations configs = new org.apache.commons.configuration2.builder.fluent.Configurations();
        PropertiesConfiguration p = null;
        try {
            if (obj instanceof String) {
                configs.properties((String) obj);
            } else if (obj instanceof File) {
                configs.properties((File) obj);
            } else if (obj instanceof URL) {
                configs.properties((URL) obj);
            } else {
                throw Exceptions.newRuntimeException("该方法不支持类型：" + obj.getClass());
            }
        } catch (Exception e) {
            LOGGER.warn("未找到或加载属性文件失败！", e.getMessage());
            throw Exceptions.newRuntimeException(e);
        }
        return p;
    }
}
