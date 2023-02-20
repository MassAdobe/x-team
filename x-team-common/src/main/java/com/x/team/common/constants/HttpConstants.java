package com.x.team.common.constants;

/**
 * 描述：HTTP STATIC POOL
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 17:26
 */
public class HttpConstants {
    /**
     * BACK JSON FORMAT
     */
    public final static String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
    /**
     * BACK XML FORMAT
     */
    public final static String APPLICATION_XML_UTF8 = "application/xml;charset=utf-8";
    /**
     * User-Agent
     */
    public final static String USER_AGENT_HEADER = "User-Agent";
    /**
     * BACK FORMAT KEY
     */
    public final static String CONTENT_TYPE_KEY = "Content-Type";
    /**
     * https 头
     */
    public final static String HTTPS_HEADER = "https://";
    /**
     * 秘钥头
     */
    public final static String AUTHORIZATION = "Authorization";
    /**
     * 移动端的user-agent-key
     */
    public final static String MOBILE_USER_AGENT_KEY = "User-Agent";
    /**
     * 移动端的user-agent-value
     */
    public final static String MOBILE_USER_AGENT_VALUE = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13F69 MicroMessenger/6.6.1 NetType/4G Language/zh_CN";
    /**
     * 国际化
     */
    public final static String ACCEPT_LANGUAGE = "Accept-Language";
    /**
     * HTTP REST请求 GET
     */
    public final static String HTTP_REQUEST_METHOD_GET = "GET";
    /**
     * HTTP REST请求 POST
     */
    public final static String HTTP_REQUEST_METHOD_POST = "POST";
    /**
     * HTTP REST请求 PUT
     */
    public final static String HTTP_REQUEST_METHOD_PUT = "PUT";
    /**
     * HTTP REST请求 DELETE
     */
    public final static String HTTP_REQUEST_METHOD_DELETE = "GET";
    /**
     * trace-id
     */
    public final static String X_REQUEST_ID = "x-request-id";
    /**
     * health checks
     */
    public final static String HEALTH_CHECKS_HEADER = "health";
    /**
     * http code status 504 request timeout
     */
    public final static Integer HTTP_STATUS_TIMEOUT = 504;
    /**
     * http code status 429 rate limit
     */
    public final static Integer HTTP_STATUS_LIMIT = 429;
    /**
     * http code status 401 unauthorized
     */
    public final static Integer HTTP_UNAUTHORIZED = 401;
    /**
     * http code status 404 not found
     */
    public final static Integer HTTP_NOT_FOUND = 404;
    /**
     * http code status 503 service unavailable
     */
    public final static Integer HTTP_SERVICE_UNAVAILABLE = 503;
    /**
     * http code status 415 unsupported media type
     */
    public final static Integer HTTP_UNSUPPORTED_MEDIA_TYPE = 415;
}
