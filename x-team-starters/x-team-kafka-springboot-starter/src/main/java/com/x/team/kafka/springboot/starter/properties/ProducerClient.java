package com.x.team.kafka.springboot.starter.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：kafka生产者配置
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProducerClient {

    /**
     * 连接地址
     */
    private String server;
    /**
     * 重试次数
     */
    private Integer retries;
    /**
     * 批大小
     */
    private Integer batchSize;
    /**
     * 缓冲内存大小
     */
    private Integer bufferMemory;
    /**
     * acknowledge
     */
    private String acks;
    /**
     * linger
     */
    private Integer linger;
    /**
     * keySerializer
     */
    private String keySerializer;
    /**
     * valueSerializer
     */
    private String valueSerializer;
}
