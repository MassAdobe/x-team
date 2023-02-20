package com.x.team.grpc.springboot.starter.envoy.constants;

/**
 * 描述：监听器常量池
 *
 * @author MassAdobe
 * @date Created in 2023/2/10 19:51
 */
public class ListenerConstants {
    /**
     * IP: 0.0.0.0
     */
    public static final String ANY_ADDRESS = "0.0.0.0";
    /**
     * IP: 127.0.0.1
     */
    public static final String LOCALHOST = "127.0.0.1";
    /**
     * 监听端口
     */
    public static final Integer LISTENER_PORT = 10408;
    /**
     * 监听器名称后缀
     */
    public static final String LISTENER_SUFFIX = "-listener";
    /**
     * HTTP STAT PREFIX 后缀
     */
    public static final String HTTP_STAT_PREFIX_SUFFIX = "-http-ingress";
    /**
     * CORS ALL
     */
    public static final String CORS_ALL = "*";
    /**
     * CORS ALLOW METHODS
     */
    public static final String CORS_ALLOW_METHODS = "GET, PUT, DELETE, POST, OPTIONS";
    /**
     * CORS ALLOW HEADERS
     */
    public static final String CORS_ALLOW_HEADERS = "x-request-id,keep-alive,user-agent,cache-control,content-type,content-transfer-encoding,x-accept-content-transfer-encoding,x-accept-response-streaming,x-user-agent,x-grpc-web,grpc-timeout,accept-language";
    /**
     * CORS MAX AGE
     */
    public static final String CORS_MAX_AGE = "17280000";
    /**
     * CORS EXPOSE HEADERS
     */
    public static final String CORS_EXPOSE_HEADERS = "grpc-status,grpc-message,grpc-status-details-bin,x-request-id";
    /**
     * LOCAL REPLY CONFIG RUNTIME KEY
     */
    public static final String RUNTIME_KEY = "key";
}
