package com.he.module.util.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.he.module.util.Strings;

public class Webs {

    public static final String session_kaptcha_key = com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY;

    @SuppressWarnings("unchecked")
    public static <T> T getRequestAttribute(ServletRequest request, String name) {
        return (T) request.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(ServletRequest request, String name) {
        return (T) getSession(request).getAttribute(name);
    }

    public static HttpSession getSession(ServletRequest request) {
        return getSession(request, true);
    }

    public static HttpSession getSession(ServletRequest request, boolean create) {
        return getSession((HttpServletRequest) request, create);
    }

    public static HttpSession getSession(HttpServletRequest request) {
        return getSession(request, true);
    }

    public static HttpSession getSession(HttpServletRequest request, boolean create) {
        return request.getSession(create);
    }

    public static String getSessionKaptcha(ServletRequest request) {
        return getSessionAttribute(request, session_kaptcha_key);
    }

    public static boolean isEqualsSessionKaptcha(ServletRequest request, String kaptcha) {
        String skaptcha = getSessionKaptcha(request);
        return kaptcha.equals(skaptcha);
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (Strings.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (Strings.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (Strings.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (Strings.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (Strings.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (Strings.isNotBlank(ip) && ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (Strings.isNotBlank(ip)) {
            String[] ips = ip.split(",");
            for (String o : ips) {
                if (!o.equals("unknown")) {
                    return o;
                }
            }
        } else {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return (request.getHeader("accept") != null && request.getHeader("accept").indexOf("application/json") > -1) ||  //
                (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1);
    }

    public static boolean isNotAjaxRequest(HttpServletRequest request) {
        return !isAjaxRequest(request);
    }

    public static boolean isFF(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").indexOf("Firefox") != -1;
    }

    public static boolean isChrome(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").indexOf("Chrome") != -1;
    }

    public static boolean isIE(HttpServletRequest request) {
        return !isFF(request) && !isChrome(request);
    }

    /**
     * 获取客户端操作系统，不够准确。
     */
    public static String getClientOS(String userAgent) {
        String cos = "unknow os";
        Pattern p = null;
        Matcher m = null;
        {
            p = Pattern.compile(".*(Windows NT 6\\.1).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "Win 7";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(Windows NT 5\\.1|Windows XP).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "WinXP";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(Windows NT 5\\.2).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "Win2003";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(Win2000|Windows 2000|Windows NT 5\\.0).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "Win2000";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(Mac|apple|MacOS8).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "MAC";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(WinNT|Windows NT).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "WinNT";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*Linux.*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "Linux";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(68k|68000).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "Mac68k";
                return cos;
            }
        }
        {
            p = Pattern.compile(".*(9x 4.90|Win9(5|8)|Windows 9(5|8)|95/NT|Win32|32bit).*");
            m = p.matcher(userAgent);
            if (m.find()) {
                cos = "Win9x";
                return cos;
            }
        }
        return cos;
    }

    public static void download(String path, String name, HttpServletResponse response, HttpServletRequest request) throws Exception {
        String downLoadName = null;
        if (name == null) {
            String suffix = path.substring(path.lastIndexOf("."), path.length());
            name = System.currentTimeMillis() + suffix;
        } else {
            name = name.replaceAll(" ", "_");
        }
        String agent = request.getHeader("USER-AGENT");
        if (null != agent && -1 != agent.indexOf("Firefox")) { // Firefox
            downLoadName = new String(name.getBytes("UTF-8"), "iso-8859-1");
        } else if (null != agent && -1 != agent.indexOf("Mozilla")) { // IE
            downLoadName = URLEncoder.encode(name, "UTF-8");
        } else {
            downLoadName = URLEncoder.encode(name, "UTF-8");
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + downLoadName);
        FileInputStream fis = null;
        OutputStream os = null;
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            fis = new FileInputStream(path);
            os = response.getOutputStream();
            while ((len = fis.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                os.close();
                fis.close();
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
