package com.x.team.wechat.springboot.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：小程序配置
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:34
 */
@Data
@Configuration
@ConfigurationProperties(prefix = WechatConfig.WECHAT_CONFIG_PREFIX)
public class WechatConfig {

    /**
     * WechatConfig前缀
     */
    public final static String WECHAT_CONFIG_PREFIX = "wechat";

    /**
     * 小程序 appId
     */
    private String appId;
    /**
     * 小程序 appSecret
     */
    private String appSecret;
}
