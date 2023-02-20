package com.gaussian.ics.sms.springboot.starter.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：阿里云sms configuration
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:57
 */
@Data
@Configuration
@ConfigurationProperties(prefix = AliSmsConfig.ALI_SMS_CONFIG_PREFIX)
public class AliSmsConfig {

    /**
     * 前缀
     */
    public final static String ALI_SMS_CONFIG_PREFIX = "sms";

    /**
     * 地域ID
     */
    private String regionId;
    /**
     * access-key
     */
    private String accessKeyId;
    /**
     * access-secret
     */
    private String accessKeySecret;

    /**
     * 获取服务client
     */
    @Bean
    public IAcsClient iAcsClient() {
        DefaultProfile defaultProfile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessKeySecret);
        return new DefaultAcsClient(defaultProfile);
    }

    /**
     * domain
     */
    public final static String ALI_SMS_SYS_DOMAIN = "dysmsapi.aliyuncs.com";
    /**
     * 系统版本
     */
    public final static String ALI_SMS_SYS_VERSION = "2017-05-25";
    /**
     * 动作
     */
    public final static String ALI_SMS_SYS_ACTION_SEND_SMS = "SendSms";
    /**
     * 待发送手机号 key
     */
    public final static String ALI_SMS_PHONE_NUM_KEY = "PhoneNumbers";
    /**
     * 阿里云短信签名 key
     */
    public final static String ALI_SMS_SIGN_NAME_KEY = "SignName";
    /**
     * 阿里云短信模板 key
     */
    public final static String ALI_SMS_TEMPLATE_CODE_KEY = "TemplateCode";
    /**
     * JSON替换内容 key
     */
    public final static String ALI_SMS_TEMPLATE_PARAM_KEY = "TemplateParam";
}
