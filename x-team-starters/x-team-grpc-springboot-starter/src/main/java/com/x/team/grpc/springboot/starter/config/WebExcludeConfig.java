package com.x.team.grpc.springboot.starter.config;

import com.x.team.grpc.springboot.starter.dto.WebExcludeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

/**
 * 描述：过滤不用拦截的接口配置读取
 *
 * @author MassAdobe
 * @date Created in 2023/2/16 16:34
 */
@Slf4j
@Configuration
@ConfigurationProperties(
        prefix = WebExcludeConfig.WEB_EXCLUDE_CONFIGURATION_PREFIX,
        ignoreInvalidFields = true
)
public class WebExcludeConfig {

    /**
     * 前缀
     */
    public final static String WEB_EXCLUDE_CONFIGURATION_PREFIX = "web";

    /**
     * 路由配置
     */
    public static volatile Map<String, WebExcludeDto> excludeUrls = new TreeMap<>();

    @Bean
    public void printWebExcludeConfiguration() {
        if (!excludeUrls.isEmpty()) {
            log.info("[web-exclude]: configuration: {}", excludeUrls.toString());
            excludeUrls.forEach((k, v) -> log.info("key: {}, value: {}", k, v));
        }
    }

    /**
     * GETTER
     */
    public synchronized static Map<String, WebExcludeDto> getExcludeUrls() {
        return excludeUrls;
    }

    /**
     * SETTER
     */
    public synchronized void setExcludeUrls(Map<String, WebExcludeDto> excludeUrls) {
        WebExcludeConfig.excludeUrls = excludeUrls;
    }
}
