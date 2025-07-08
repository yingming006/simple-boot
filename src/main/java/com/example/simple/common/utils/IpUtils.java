package com.example.simple.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * IP地址工具类
 */
public class IpUtils {

    /**
     * 获取当前请求的IP地址。
     * <p>
     * 此方法会尝试从RequestContextHolder获取当前的HttpServletRequest。
     * 如果无法获取（例如，在非Web请求的线程中调用），则会返回 "未知IP"。
     *
     * @return 客户端IP地址，或者在无法获取时返回 "未知IP"
     */
    public static String getCurrentIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "未知IP";
        }
        HttpServletRequest request = attributes.getRequest();
        return getIpAddress(request);
    }

    /**
     * 从HttpServletRequest中获取客户端的真实IP地址。
     * <p>
     * 该方法会依次从 x-forwarded-for, Proxy-Client-IP, WL-Proxy-Client-IP 头中获取IP。
     * 如果都获取不到，则使用 request.getRemoteAddr() 作为后备。
     * 同时处理了通过localhost访问时返回 "0:0:0:0:0:0:0:1" 的情况。
     *
     * @param request HttpServletRequest对象，可以为null
     * @return 客户端IP地址，如果request为null则返回 "未知IP"
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "未知IP";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (isIpInvalid(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isIpInvalid(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isIpInvalid(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，x-forwarded-for的值可能是一串IP地址，取第一个非unknown的有效IP。
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 检查IP地址字符串是否无效。
     *
     * @param ip IP地址字符串
     * @return 如果IP为null、空或"unknown"，则返回true；否则返回false。
     */
    private static boolean isIpInvalid(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}