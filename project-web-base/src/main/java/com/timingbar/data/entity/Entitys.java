package com.timingbar.data.entity;

import java.lang.reflect.Field;

public class Entitys {

    private static String trans(String type) {
        type = type.toLowerCase();
        if (type.equals("string")) {
            type = "string";
        } else if (type.equals("integer")) {
            type = "int";
        } else if (type.equals("long")) {
            type = "int";
        } else if (type.equals("date")) {
            type = "date";
        }
        return type;
    }

    @SuppressWarnings("rawtypes")
    public static void getExtField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("{name:'id',type:'int'},\n");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            sb.append("{");
            sb.append("name:").append("'" + field.getName() + "',");
            sb.append("type:").append("'" + trans(field.getType().getSimpleName()) + "'");
            sb.append("}");
            if (i < fields.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("]");
        System.out.println(sb);
    }

    @SuppressWarnings("rawtypes")
    public static void getExtColumns(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("{");
        sb.append("id:").append("'id',");
        sb.append("dataIndex:").append("'id',");
        sb.append("sortable:").append("false,");
        sb.append("hidden:").append("true");
        sb.append("},\n");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            sb.append("{");
            sb.append("text:").append("'" + field.getName() + "'").append(",");
            sb.append("dataIndex:").append("'" + field.getName() + "'").append(",");
            sb.append("sortable:").append("false").append(",");
            sb.append("hidden:").append("false");
            sb.append("}");
            if (i < fields.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("]");
        System.out.println(sb);
    }

    @SuppressWarnings("rawtypes")
    public static void getCqcisColumns(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("{");
        sb.append("id:").append("'id',");
        sb.append("text:").append("'id',");
        sb.append("name:").append("'id',");
        sb.append("sortable:").append("false,");
        sb.append("hidden:").append("true");
        sb.append("},\n");
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            sb.append("{");
            sb.append("text:").append("'" + field.getName() + "'").append(",");
            sb.append("name:").append("'" + field.getName() + "'");
            sb.append("}");
            if (i < fields.length - 1) {
                sb.append(",\n");
            }
        }
        sb.append("]");
        System.out.println(sb);
    }
}
