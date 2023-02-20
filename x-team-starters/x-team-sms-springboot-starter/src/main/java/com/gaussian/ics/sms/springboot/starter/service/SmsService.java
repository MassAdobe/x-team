package com.gaussian.ics.sms.springboot.starter.service;

import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;

/**
 * 描述：短信服务类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:57
 */
public interface SmsService {

    /**
     * 使用云片短信机 往给定手机发送手机验证码
     */
    Result<SmsSingleSend> sendSmsCodeByYunPian(String apiKey, String smsContent, String phoneNum);

    /**
     * 使用阿里云短息 往给定手机发送验证码
     */
    void sendSmsCodeByAli(String phoneNum, String signName, String templateCode, String param);
}
