package com.he.module.util.spring;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.google.common.base.Charsets;
import com.he.module.Constants;
import com.he.module.util.Exceptions;
import com.he.module.util.Jsons;
import com.he.module.util.Logs;
import com.he.module.util.Strings;
import com.he.module.util.web.Servlets;
import com.he.module.util.web.Webs;

public class HandlerException extends SimpleMappingExceptionResolver {
    private static final int    ERROR_STATUSCODE                            = 500;
    private static final String ERROR_MSG_500                               = "服务器忙碌,请稍后再试!";
    private static final String ERROR_MSG_DEFAULT_NO_PERMS                  = "对不起，您没有权限访问该资源!";
    public static final String  LOG_ERROR_FORMAT_CLASS                      = "错误类名:%s";
    public static final String  LOG_ERROR_FORMAT_METHOD                     = "错误方法:%s";
    public static final String  LOG_ERROR_FORMAT_ARG                        = "方法参数:%s";
    public static final String  LOG_ERROR_FORMAT_EXCEPTION                  = "异常信息:%s";
    public static final String  LOG_ERROR_FORMAT_URL                        = "请求地址:%s";
    public static final String  LOG_ERROR_FORMAT_PARAM                      = "请求参数:%s";

    public static final String  LOG_ERROR_FORMAT_CLASS_METHOD_EXCEPTION     = LOG_ERROR_FORMAT_CLASS + "\n" + LOG_ERROR_FORMAT_METHOD + "\n" + LOG_ERROR_FORMAT_EXCEPTION;
    public static final String  LOG_ERROR_FORMAT_CLASS_METHOD_ARG_EXCEPTION = LOG_ERROR_FORMAT_CLASS + "\n" + LOG_ERROR_FORMAT_METHOD + "\n" + LOG_ERROR_FORMAT_ARG + "\n" + LOG_ERROR_FORMAT_EXCEPTION;

    public static final String  LOG_ERROR_FORMAT_URL_PARAM                  = LOG_ERROR_FORMAT_URL + "\n" + LOG_ERROR_FORMAT_PARAM;
    public static final String  LOG_ERROR_FORMAT_URL_PARAM_EXCEPTION        = LOG_ERROR_FORMAT_URL + "\n" + LOG_ERROR_FORMAT_PARAM + "\n" + LOG_ERROR_FORMAT_EXCEPTION;

    public static final Charset CHARSET_DEFAULT                             = Charsets.UTF_8;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = null;
        // 解析
        String url = Servlets.getRequestURL(request);
        String params = Jsons.toJson(request.getParameterMap());
        String msg_log = "\n ";
        // 返回
        Integer statusCode = ERROR_STATUSCODE;
        String msg_out = ERROR_MSG_500;
        ex = (Exception) Exceptions.getRootCause(ex);
        if (ex instanceof Exception) {
            String msg_log_url = Strings.format(LOG_ERROR_FORMAT_URL_PARAM, url, params);
            if (ex instanceof RuntimeException) {
                // 底层甩上来的异常，添加URL和PARAM
                msg_log += msg_log_url + "\n" + ex.getMessage();
            } else {
                // 容器以及Controller的异常
                if (null != handler) { // 获取到对应的Handler时候，无法解析方法名和参数，
                    Method handlerMethod = ((HandlerMethod) handler).getMethod();
                    String method = handlerMethod.getName();
                    String clazz = handlerMethod.getDeclaringClass().getSimpleName();
                    msg_log += "\n" + msg_log_url + "\n" + Strings.format(LOG_ERROR_FORMAT_CLASS_METHOD_EXCEPTION, clazz, method, ex.getMessage());
                } else {// 获取不到对应的Handler时候，则只记录URL和PARAM
                    msg_log += Strings.format(LOG_ERROR_FORMAT_URL_PARAM_EXCEPTION, url, params, ex.getMessage());
                }
            }
            if (ex instanceof AuthorizationException) {// 没登陆
                msg_out = ERROR_MSG_DEFAULT_NO_PERMS;
            }
        }
        // 根据调试决定输出
        if (Constants.IS_DEBUG) {
            msg_log += "\n返回状态:" + statusCode + "\n返回消息:" + msg_out;
            msg_out = msg_log.replaceAll("\n", "<br/>");
            msg_log += "\n异常详情";
            // msg_out += Jsons.toJson(ex.getStackTrace());
        }
        if (Webs.isAjaxRequest(request)) {
            applyStatusCodeIfPossible(request, response, statusCode);// Ajax请求,返回200,防止页面报错
            try {
                Servlets.writeJsonErrorData(response, msg_out, statusCode);
            } catch (IOException ioe) {
                Logs.error(this, "返回JSON异常信息错误!", ioe);
            }
        }
        // 记录日志
        Logs.error(this, msg_log, ex);
        // super.doResolveException(request, response, handler, ex);
        // 以下：
        String viewName = determineViewName(ex, request);
        if (viewName != null) {
            statusCode = determineStatusCode(request, viewName);
            if (statusCode != null) {
                applyStatusCodeIfPossible(request, response, statusCode);
            }
            ex = Exceptions.newRuntimeException(msg_out, ex);// 包裹并输出,必须在determineViewName后面，否则将根据RuntimeException去找输出页面。
            mv = getModelAndView(viewName, ex, request);
        }
        return mv;
    }
  /**  @formatter:off
   spring的异常处理            SimpleMappingExceptionResolver 
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc" xsi:schemaLocation="http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
        <!-- springmvc异常处理 -->
        <!-- 定义无需Controller的url<->view直接映射 -->
        <mvc:view-controller path="/error/403" view-name="/error/403" />
        <mvc:view-controller path="/error/404" view-name="/error/404" />
        <mvc:view-controller path="/error/500" view-name="/error/500" />

        <!-- 
        <bean class="com.he.spring.handler.HandlerException">
         -->
        <bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
            <property name="defaultErrorView" value="/error/500"></property>
            <property name="defaultStatusCode" value="500" />
            <property name="statusCodes">
                <props>
                    <prop key="/error/403">403</prop>
                    <prop key="/error/404">404</prop>
                    <prop key="/error/500">500</prop>
                </props>
            </property>
            <property name="exceptionMappings">
                <props>
                    <prop key="org.apache.shiro.authz.AuthorizationException">/error/403</prop>
                    <prop key="org.springframework.web.HttpRequestMethodNotSupportedException">/error/404</prop>
                    <prop key="java.lang.Throwable">/error/500</prop>
                </props>
            </property>
        </bean>

    </beans>
*/
}
