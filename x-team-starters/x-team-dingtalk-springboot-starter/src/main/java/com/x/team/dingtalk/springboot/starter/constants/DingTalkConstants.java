package com.x.team.dingtalk.springboot.starter.constants;

/**
 * 描述：钉钉群消息发送常量池
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 17:49
 */
public class DingTalkConstants {
    /**
     * 消息类型(text)
     */
    public final static String DING_TALK_MSG_TYPE_TEXT = "text";
    /**
     * 调用接口路径
     */
    public final static String DING_TALK_CALL_URL = "https://oapi.dingtalk.com/robot/send?access_token=%s&timestamp=%s&sign=%s";
    /**
     * 加密选型
     */
    public final static String ENCODE_TYPE = "HmacSHA256";
    /**
     * 成功
     */
    public final static String SUCCESS_MSG = "SUCCESS";
    /**
     * 数据错误
     */
    public final static String FAIL_PARAM_ERROR = "数据错误";
    /**
     * 加密错误
     */
    public final static String FAIL_ENCODE_ERROR = "加密错误";
    /**
     * 发送请求错误(JSON解析)
     */
    public final static String FAIL_SEND_JSON_ERROR = "发送请求错误(JSON解析)";
    /**
     * 响应请求错误(JSON解析)
     */
    public final static String FAIL_RECEIVE_JSON_ERROR = "发送请求错误(JSON解析)";
    /**
     * 算法encoding过期时长
     */
    public final static Long ENCODING_EXPIRE_TIME_PERIOD = 50 * 60L;
}
