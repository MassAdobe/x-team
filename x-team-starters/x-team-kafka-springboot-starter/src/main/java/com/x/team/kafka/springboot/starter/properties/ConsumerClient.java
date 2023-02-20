package com.x.team.kafka.springboot.starter.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：kafka消费者配置
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerClient {
    /**
     * 连接服务地址
     */
    private String server;
    /**
     * 组ID
     */
    private String groupId;
    /**
     * 自动commit
     */
    private Boolean enableAutoCommit;
    /**
     * 偏移量
     */
    private String offsetReset;
    /**
     * 内部偏移量
     */
    private Integer autoCommitInterval;
    /**
     * currency
     */
    private Integer currency;
    /**
     * keyDeserializer
     */
    private String keyDeserializer;
    /**
     * valueDeserializer
     */
    private String valueDeserializer;
}
