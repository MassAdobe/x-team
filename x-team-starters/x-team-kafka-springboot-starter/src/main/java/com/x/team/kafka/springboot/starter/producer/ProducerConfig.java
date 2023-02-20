package com.x.team.kafka.springboot.starter.producer;

import com.x.team.kafka.springboot.starter.config.KafkaConfiguration;
import com.x.team.kafka.springboot.starter.properties.ProducerClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：生产者配置
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:02
 */
@Slf4j
@Data
@EnableKafka
@Configuration
public class ProducerConfig {

    /**
     * 报错信息
     */
    private final static String KAFKA_PRODUCER_CONFIG_ERROR = "KAFKA生产者配置错误";

    @Resource
    private KafkaConfiguration kafkaConfiguration;

    /**
     * 配置
     */
    private Map<String, Object> producerConfigs(String producerKey) {
        if (Objects.nonNull(this.kafkaConfiguration.getProducers()) && !this.kafkaConfiguration.getProducers().isEmpty()) {
            if (this.kafkaConfiguration.getProducers().containsKey(producerKey)) {
                ProducerClient producerClient = this.kafkaConfiguration.getProducers().get(producerKey);
                log.info("【当前获取到的配置(kafka-producer)】: kafka-producer: {}", producerClient);
                Map<String, Object> props = new HashMap<>(16);
                props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerClient.getServer());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, producerClient.getRetries());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, producerClient.getBatchSize());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, producerClient.getAcks());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, producerClient.getLinger());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, producerClient.getBufferMemory());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerClient.getKeySerializer());
                props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerClient.getValueSerializer());
                return props;
            }
        }
        throw new RuntimeException(KAFKA_PRODUCER_CONFIG_ERROR);
    }

    /**
     * 工厂类
     */
    private ProducerFactory<String, String> producerFactory(String producerKey) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(producerKey));
    }

    /**
     * 模板类
     */
    public KafkaTemplate<String, String> getProducer(String producerKey) {
        return new KafkaTemplate<>(producerFactory(producerKey));
    }

}
