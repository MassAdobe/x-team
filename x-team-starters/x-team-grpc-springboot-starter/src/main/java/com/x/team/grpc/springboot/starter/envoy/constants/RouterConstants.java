package com.x.team.grpc.springboot.starter.envoy.constants;

/**
 * 描述：路由器常量池
 *
 * @author MassAdobe
 * @date Created in 2023/2/13 15:45
 */
public class RouterConstants {
    /**
     * 路由器名称后缀
     */
    public static final String ROUTER_SUFFIX = "-router";
    /**
     * 返回请求TraceId
     */
    public static final String ROUTER_REQUEST_ID_VALUE = "%REQ(x-request-id)%";
    /**
     * RATE LIMIT HEADER KEY
     */
    public static final String RATE_LIMIT_HEADER_KEY = "x-rate-limit";
    /**
     * RATE LIMIT HEADER KEY
     */
    public static final String RATE_LIMIT_HEADER_VALUE = "on";
    /**
     * VIRTUAL HOSTS SUFFIX
     */
    public static final String VIRTUAL_HOSTS_SUFFIX = "-vhost";
    /**
     * ROUTE DOMAIN
     */
    public static final String ROUTE_DOMAIN_ALL = "*";
    /**
     * ALL API PREFIX INTERCEPTOR
     */
    public static final String MATCH_PREFIX_ALL = "/";
    /**
     * HEALTH API PREFIX INTERCEPTOR
     */
    public static final String MATCH_HEALTH_PREFIX = "grpc.health.server.Health";
    /**
     * ROUTE TIMEOUT SECOND
     */
    public static final Long ROUTE_TIMEOUT_MINUTE = 3L;
    /**
     * ROUTE REQUEST HEALTH LIMIT SUFFIX
     */
    public static final String ROUTE_REQUEST_HEALTH_LIMIT_SUFFIX = "-health";
    /**
     * ROUTE REQUEST LOCAL LIMIT SUFFIX
     */
    public static final String ROUTE_REQUEST_LOCAL_LIMIT_SUFFIX = "-rate-limit";
}
