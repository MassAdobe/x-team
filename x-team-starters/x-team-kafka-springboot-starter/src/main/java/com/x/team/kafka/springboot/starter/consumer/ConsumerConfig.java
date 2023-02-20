package com.x.team.kafka.springboot.starter.consumer;

import com.x.team.kafka.springboot.starter.config.KafkaConfiguration;
import com.x.team.kafka.springboot.starter.properties.ConsumerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：消费者配置
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:02
 */
@Slf4j
@EnableKafka
@Configuration
public class ConsumerConfig {

    /**
     * 报错信息
     */
    private final static String KAFKA_CONSUMER_CONFIG_ERROR = "KAFKA消费者配置错误";

    @Resource
    private KafkaConfiguration kafkaConfiguration;

    /**
     * 模板工厂
     */
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> getConsumer(String consumerKey) {
        if (Objects.nonNull(this.kafkaConfiguration.getConsumers()) && !this.kafkaConfiguration.getConsumers().isEmpty()) {
            if (this.kafkaConfiguration.getConsumers().containsKey(consumerKey)) {
                ConsumerClient consumerClient = this.kafkaConfiguration.getConsumers().get(consumerKey);
                log.info("【当前获取到的配置(kafka-consumer)】: kafka-consumer: {}", consumerClient);
                // 配置
                Map<String, Object> props = new HashMap<>(16);
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerClient.getServer());
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, consumerClient.getGroupId());
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerClient.getEnableAutoCommit());
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerClient.getOffsetReset());
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerClient.getAutoCommitInterval());
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerClient.getKeyDeserializer());
                props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerClient.getValueDeserializer());
                // 工厂
                DefaultKafkaConsumerFactory<Object, Object> objectObjectDefaultKafkaConsumerFactory
                        = new DefaultKafkaConsumerFactory<>(props);
                // 创建工厂
                ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
                factory.setConsumerFactory(objectObjectDefaultKafkaConsumerFactory);
                factory.setConcurrency(consumerClient.getCurrency());
                factory.getContainerProperties().setPollTimeout(6000);
                factory.setBatchListener(true);
                // 设置提交偏移量的方式
                factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
                return factory;
            }
        }
        throw new RuntimeException(KAFKA_CONSUMER_CONFIG_ERROR);
    }
}
