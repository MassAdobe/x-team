package com.x.team.config.springboot.starter.config;

import com.x.team.config.springboot.starter.dto.ClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：client registry
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 15:02
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = XTeamClientRegistry.GRPC_CLIENT_POOL_PREFIX)
public class XTeamClientRegistry {

    public final static String GRPC_CLIENT_POOL_PREFIX = "grpc";

    /**
     * 路由配置
     */
    public static volatile Map<String, ClientProperties> client = new ConcurrentHashMap<>();

    public void setClient(Map<String, ClientProperties> client) {
        XTeamClientRegistry.client = client;
    }

    @Bean
    public void figureOutConfiguration() {
        log.info("【当前获取到的配置】: grpc-pool: {}", client.toString());
    }

}
