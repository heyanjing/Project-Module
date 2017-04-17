package com.he.module.util.spring;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class Controllers {

    public static String forward(String url) {
        return "forward:" + url;
    }

    public static String redirect(Object obj) {
        return redirect(String.valueOf(obj));
    }

    public static String redirect(String url) {
        return "redirect:" + url;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getSession() {
        return getSession(false);
    }

    public static HttpSession getSession(boolean create) {
        return getRequest().getSession(create);
    }

}
