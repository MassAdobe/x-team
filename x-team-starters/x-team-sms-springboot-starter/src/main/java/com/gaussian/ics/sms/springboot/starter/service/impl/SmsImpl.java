package com.gaussian.ics.sms.springboot.starter.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.gaussian.ics.sms.springboot.starter.config.AliSmsConfig;
import com.gaussian.ics.sms.springboot.starter.service.SmsService;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 描述：短信服务实现类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:57
 */
@Slf4j
@Service
public class SmsImpl implements SmsService {

    @Autowired
    private IAcsClient iAcsClient;

    /**
     * 使用云片短信机 往给定手机发送手机验证码
     * <p>
     * ab9c2d1391169a78ada82af68937b966
     */
    @Override
    public Result<SmsSingleSend> sendSmsCodeByYunPian(String apiKey, String smsContent, String phoneNum) {
        YunpianClient yunpianClient = new YunpianClient(apiKey).init();
        Map<String, String> param = yunpianClient.newParam(2);
        param.put(YunpianClient.MOBILE, phoneNum);
        param.put(YunpianClient.TEXT, smsContent);
        Result<SmsSingleSend> result = yunpianClient.sms().single_send(param);
        log.info("【发送短信】: 给手机号: {}, 发送: {}, 发送结果: {}", phoneNum, smsContent, result.getMsg());
        return result;
    }

    /**
     * 使用阿里云短信 往给定手机发送验证码
     */
    @Override
    public void sendSmsCodeByAli(String phoneNum, String signName, String templateCode, String param) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(AliSmsConfig.ALI_SMS_SYS_DOMAIN);
        request.setSysVersion(AliSmsConfig.ALI_SMS_SYS_VERSION);
        request.setSysAction(AliSmsConfig.ALI_SMS_SYS_ACTION_SEND_SMS);
        request.putQueryParameter(AliSmsConfig.ALI_SMS_PHONE_NUM_KEY, phoneNum);
        request.putQueryParameter(AliSmsConfig.ALI_SMS_SIGN_NAME_KEY, signName);
        request.putQueryParameter(AliSmsConfig.ALI_SMS_TEMPLATE_CODE_KEY, templateCode);
        request.putQueryParameter(AliSmsConfig.ALI_SMS_TEMPLATE_PARAM_KEY, param);
        try {
            CommonResponse response = iAcsClient.getCommonResponse(request);
            log.info("【使用阿里云短信】: 往给定手机发送验证码结果: {}", response.getData());
        } catch (ClientException e) {
            log.error("【使用阿里云短信】: 往给定手机发送验证码: error: {}", e.getMessage());
        }
    }
}
