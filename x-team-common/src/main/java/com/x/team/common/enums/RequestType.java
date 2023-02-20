package com.x.team.common.enums;

import lombok.ToString;

/**
 * 描述：请求种类
 *
 * @author MassAdobe
 * @date Created in 2023/2/13 20:13
 */
@SuppressWarnings("all")
@ToString
public enum RequestType {

    FROM_OUTSIDE("FO", "外部请求", "FROM OUTSIDE"),
    INNER_CALL("IC", "内部调用", "INNER CALL"),
    HEALTH_CHECK("HC", "健康检查", "HEALTH CHECK"),
    SCHEDULE_CALL("SC", "定时任务请求", "SCHEDULE CALL"),
    ENVOY_CONFIG("EC", "ENVOY获取信息", "ENVOY CONFIGURATION"),
    NO_HEADER("NH", "毋需头信息", "WITHOUT HEADER"),
    ERROR_HEADER("EH", "头信息错误", "ERROR HEADER");

    /**
     * 编码
     */
    private String code;
    /**
     * 中文描述
     */
    private String zhDescription;
    /**
     * 英文描述
     */
    private String enDescription;

    RequestType(String code, String zhDescription, String enDescription) {
        this.code = code;
        this.zhDescription = zhDescription;
        this.enDescription = enDescription;
    }

    /**
     * 通过CODE获取请求种类枚举
     */
    public static RequestType getRequestType(String code) {
        for (RequestType ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return ERROR_HEADER;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getZhDescription() {
        return zhDescription;
    }

    public void setZhDescription(String zhDescription) {
        this.zhDescription = zhDescription;
    }

    public String getEnDescription() {
        return enDescription;
    }

    public void setEnDescription(String enDescription) {
        this.enDescription = enDescription;
    }
}
