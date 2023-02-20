package com.x.team.kafka.springboot.starter.config;

import com.x.team.common.constants.KafkaConstants;
import com.x.team.kafka.springboot.starter.properties.ConsumerClient;
import com.x.team.kafka.springboot.starter.properties.ProducerClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述：kafka配置
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:02
 */
@Slf4j
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = KafkaConstants.KAFKA_CONFIGURATION_PREFIX)
public class KafkaConfiguration {

    /**
     * kafka生产者注册配置
     */
    private ConcurrentHashMap<String, ConsumerClient> consumers;

    /**
     * kafka生产者注册配置
     */
    private ConcurrentHashMap<String, ProducerClient> producers;
}
