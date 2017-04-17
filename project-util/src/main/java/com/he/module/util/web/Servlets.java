package com.he.module.util.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.he.module.Constants;
import com.he.module.bean.Results;
import com.he.module.util.Encodes;
import com.he.module.util.Jsons;
import com.he.module.util.MediaTypes;
import com.he.module.util.Strings;

/**
 * Http与Servlet工具类.
 */
public class Servlets {

    // -- 常用数值定义 --//
    public static final long ONE_YEAR_SECONDS = 60 * 60 * 24 * 365;

    /**
     * 设置客户端缓存过期时间 的Header.
     */
    public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
        // Http 1.0 header, set a fix expires date.
        response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + expiresSeconds * 1000);
        // Http 1.1 header, set a time after now.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的Header.
     */
    public static void setNoCacheHeader(HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader(HttpHeaders.EXPIRES, 1L);
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
        // Http 1.1 header
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
    }

    public static void setContentType(HttpServletResponse response, String contentType) {
        response.setHeader("Content-Type", contentType);
    }

    public static void setContentTypeJson(HttpServletResponse response) {
        setContentType(response, MediaTypes.APPLICATION_JSON_UTF_8_VALUE);
    }

    /**
     * 设置LastModified Header.
     */
    public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedDate);
    }

    /**
     * 设置Etag Header.
     */
    public static void setEtag(HttpServletResponse response, String etag) {
        response.setHeader(HttpHeaders.ETAG, etag);
    }

    /**
     * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
     * 如果无修改, checkIfModify返回false ,设置304 not modify status.
     * 
     * @param lastModified 内容的最后修改时间.
     */
    public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response, long lastModified) {
        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }
        return true;
    }

    /**
     * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
     * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
     * 
     * @param etag 内容的ETag.
     */
    public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
        String headerValue = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (headerValue != null) {
            boolean conditionSatisfied = false;
            if (!"*".equals(headerValue)) {
                StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken();
                    if (currentToken.trim().equals(etag)) {
                        conditionSatisfied = true;
                    }
                }
            } else {
                conditionSatisfied = true;
            }

            if (conditionSatisfied) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader(HttpHeaders.ETAG, etag);
                return false;
            }
        }
        return true;
    }

    /**
     * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
     * 返回的结果的Parameter名已去除前缀.
     */
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Validate.notNull(request, "Request must not be null");
        Enumeration<String> paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }

    /**
     * 组合Parameters生成Query String的Parameter部分, 并在paramter name上加上prefix.
     * 
     * @see #getParametersStartingWith
     */
    public static String encodeParameterStringWithPrefix(Map<String, Object> params, String prefix) {
        if (params == null || params.size() == 0) {
            return "";
        }

        if (prefix == null) {
            prefix = "";
        }

        StringBuilder queryStringBuilder = new StringBuilder();
        Iterator<Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            queryStringBuilder.append(prefix).append(entry.getKey()).append('=').append(entry.getValue());
            if (it.hasNext()) {
                queryStringBuilder.append('&');
            }
        }
        return queryStringBuilder.toString();
    }

    /**
     * 客户端对Http Basic验证的 Header进行编码.
     */
    public static String encodeHttpBasic(String userName, String password) {
        String encode = userName + ":" + password;
        return "Basic " + Encodes.encodeBase64(encode.getBytes());
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     * 
     * @param fileName 下载后的文件名.
     * @throws Exception
     */
    public static void setFileDownloadHeader(String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        setFileDownloadHeader(fileName, null, request, response);
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     * 
     * @param fileName 下载后的文件名.
     * @throws Exception
     */
    public static void setFileDownloadHeader(String fileName, Object length, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开
        response.setContentType(MediaTypes.APPLICATION_OCTET_STREAM_VALUE);
        if (Webs.isIE(request)) {
            fileName = Encodes.encodeUrl(fileName);
        } else {
            fileName = Strings.newString(fileName, Charsets.ISO_8859_1);
        }
        if (length != null) {
            response.setHeader("Content-Length", String.valueOf(length));
        }
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
    }

    public static void setContentLength(Object length, HttpServletResponse response) {
        response.setHeader("Content-Length", String.valueOf(length));
    }

    /**
     * 获取请求的URL
     */
    public static String getRequestURL(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    public static void writeJsonData(HttpServletResponse response, Object value, int sc) throws IOException {
        PrintWriter writer = null;
        try {
            String jsonString = "";
            if (value != null) {
                jsonString = Jsons.toJson(value);
            }
            setContentTypeJson(response);
            setNoCacheHeader(response);
            setStatus(response, sc);
            writer = response.getWriter();
            writer.write(jsonString);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public static void writeJsonData(HttpServletResponse response) throws IOException {
        writeJsonData(response, null, 200);
    }

    public static void writeJsonData(HttpServletResponse response, int sc) throws IOException {
        writeJsonData(response, null, sc);
    }

    public static void writeJsonErrorData(HttpServletResponse response, Map<String, Object> data, String msg, int status) throws IOException {
        if (data == null) {
            data = Maps.newHashMap();
        }
        data.put("debug", Constants.IS_DEBUG);
        data.put("status", status);
        writeJsonData(response, Results.failureWithData(data, msg), 500);
    }

    public static void writeJsonErrorData(HttpServletResponse response, String msg, int status) throws IOException {
        writeJsonErrorData(response, null, msg, status);
    }

    public static void writeJsonDataWithOs(HttpServletResponse response, String value) throws Exception {
        OutputStream os = null;
        try {
            String jsonString = Jsons.toJson(value);
            Servlets.setContentTypeJson(response);
            Servlets.setNoCacheHeader(response);
            os = response.getOutputStream();
            os.write(jsonString.getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

    public static void setError(HttpServletResponse response) {
        setError(response, 500);
    }

    public static void setError(HttpServletResponse response, int sc) {
        response.setStatus(sc);
    }

    public static void setStatus(HttpServletResponse response, int sc) {
        response.setStatus(sc);
    }

    public static void setStatus403(HttpServletResponse response) {
        setStatus(response, 403);
    }

    public static void setStatus404(HttpServletResponse response) {
        setStatus(response, 404);
    }

    public static void setStatus500(HttpServletResponse response) {
        setStatus(response, 500);
    }

}
