package com.x.team.common.enums;

import com.x.team.common.constants.LanguageConstants;
import com.x.team.common.dto.rest.NoticeDto;
import lombok.ToString;

/**
 * 描述：报错枚举
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 14:40
 */
@ToString
public enum ErrorCodeMsg {
    // 系统级别(0,0->999)
    SUCCESS("0", "成功", "SUCCESS"),
    PARAM_ERROR("1", "参数错误", "PARAMS ERROR"),
    SERVER_ERROR("2", "服务错误", "SERVICE ERROR"),
    UNKNOWN_ERROR("3", "未知错误", "UNKNOWN ERROR"),
    SYSTEM_ILLEGAL_REQUEST("4", "非法访问", "ILLEGAL REQUEST"),
    SYSTEM_TOKEN_EXPIRE_ERROR("5", "登录过期", "SIGN IN EXPIRED"),
    SYSTEM_TOKEN_DECODE_ERROR("6", "无权限", "SIGN IN VALIDATE EXCEPTION"),
    REDIS_ERROR("7", "缓存错误", "CACHE ERROR"),
    REDIS_INCR_ERROR("8", "缓存原子错误", "CACHE INCR ERROR"),
    WECHAT_ERROR("9", "微信错误", "WECHAT ERROR"),
    JSON_ERROR("10", "JSON解析错误", "JSON ENCODE/DECODE ERROR"),
    GRPC_ERROR("11", "GRPC错误", "GRPC ERROR"),
    OSS_ERROR("12", "对象存储错误", "OSS ERROR"),
    TOKEN_ERROR("13", "令牌错误", "TOKEN ERROR"),
    TIMEOUT_ERROR("14", "请求超时", "REQUEST TIMEOUT"),
    RATE_LIMIT_ERROR("15", "请求过于频繁", "RATE LIMIT"),
    NO_HEADER_ERROR("16", "没有头部错误", "REQUEST WITHOUT HEADER"),
    ENVOY_ERROR("17", "envoy错误", "ENVOY ERROR"),
    UNAUTHORIZED_ERROR("18", "未验证错误", "UNAUTHORIZED ERROR"),
    NOT_FOUND_ERROR("19", "未找到", "NOT FOUND ERROR"),
    SERVICE_UNAVAILABLE_ERROR("20", "服务不可触达", "SERVICE UNAVAILABLE ERROR"),
    UNSUPPORTED_MEDIA_TYPE_ERROR("21", "不支持的请求种类", "UNSUPPORTED MEDIA TYPE ERROR"),

    // 业务错误
    ;

    /**
     * 状态
     */
    private String status;
    /**
     * 中文
     */
    private String zhMessage;
    /**
     * 英文
     */
    private String enMessage;

    /**
     * 构造器
     *
     * @param status    状态
     * @param zhMessage 中文
     * @param enMessage 英文
     */
    ErrorCodeMsg(String status, String zhMessage, String enMessage) {
        this.status = status;
        this.zhMessage = zhMessage;
        this.enMessage = enMessage;
    }

    /**
     * 获取数据
     *
     * @param status 状态
     * @return 枚举
     */
    public static ErrorCodeMsg getValue(String status) {
        for (ErrorCodeMsg ele : values()) {
            if (status.equals(ele.getStatus())) {
                return ele;
            }
        }
        return null;
    }

    /**
     * 根据国际化获取报错
     */
    public static NoticeDto getError(String i18n, ErrorCodeMsg errorCodeMsg) {
        switch (i18n) {
            case LanguageConstants
                    .EN_US:
                // 美国英文
            case LanguageConstants
                    .EN_GB:
                // 英国英文
                return NoticeDto.builder()
                        .status(errorCodeMsg.getStatus())
                        .msg(errorCodeMsg.getEnMessage())
                        .build();
            default:
                // 默认中文
                return NoticeDto.builder()
                        .status(errorCodeMsg.getStatus())
                        .msg(errorCodeMsg.getZhMessage())
                        .build();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public void setZhMessage(String zhMessage) {
        this.zhMessage = zhMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }

    public void setEnMessage(String enMessage) {
        this.enMessage = enMessage;
    }
}
