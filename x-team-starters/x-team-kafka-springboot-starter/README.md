# kafka-starter

---

## Maven

```xml
<dependency>
    <groupId>com.gaussian</groupId>
    <artifactId>ics-kafka-springboot-starter</artifactId>
</dependency>
```

---

## 生产者

### 配置：

```yaml
kafka:
  # 生产者
  producers:
    # 第一个生产者
    firstProducer:
      server: 127.0.0.1:9092,127.0.0.2:9092
      retries: 3
      batchSize: 16384
      bufferMemory: 33554432
      acks: all
      linger: 1
      keySerializer: org.apache.kafka.common.serialization.StringSerializer
      valueSerializer: org.apache.kafka.common.serialization.StringSerializer
    # 第二个生产者
    secondProducer:
      server: 127.0.0.1:9092,127.0.0.2:9092
      retries: 3
      batchSize: 16384
      bufferMemory: 33554432
      acks: all
      linger: 1
      keySerializer: org.apache.kafka.common.serialization.StringSerializer
      valueSerializer: org.apache.kafka.common.serialization.StringSerializer
```

### 使用:

#### 在项目中创建KafkaConfig.java

```java

@Configuration
public class KafkaConfig {

    /**
     * 注入生产者
     */
    @Autowired
    private ProducerConfig producerConfig;

    /**
     * 生产者: firstProducer
     * <p/>
     * "firstProducer"最好写成常量池
     */
    @Bean(KafkaCrewConstant.FIRST_PRODUCER)
    public KafkaTemplate<String, String> firstProducer() {
        return this.producerConfig.getProducer(KafkaCrewConstant.FIRST_PRODUCER);
    }

    /**
     * 生产者: secondProducer
     */
    @Bean(KafkaCrewConstant.SECOND_PRODUCER)
    public KafkaTemplate<String, String> secondProducer() {
        return this.producerConfig.getProducer(KafkaCrewConstant.SECOND_PRODUCER);
    }
}
```

#### 业务中使用

```java
    /**
 * kafka的模板(第一个生产者)
 */
@Autowired
private KafkaTemplate<String, String> firstProducer;

/**
 * kafka的模板(第二个生产者)
 */
@Autowired
private KafkaTemplate<String, String> secondProducer;

/**
 * 生产者发送
 */
@Override
public void kafkaProducers(){
    // 创建日志TraceId
    SnLog.setBegin();
    this.firstProducer.send(KafkaConstants.TOPIC_1,"first","first:"+System.currentTimeMillis());
    this.secondProducer.send(KafkaConstants.TOPIC_2,"second","second:"+System.currentTimeMillis());
    // 注销日志TraceId
    SnLog.removeBegin();
}
```

---

## 消费者

### 配置：

```yaml
kafka:
  # 消费者
  consumer:
    # 第一个消费者
    firstConsumer:
      server: 127.0.0.1:9092,127.0.0.2:9092
      groupId: consumerGroup1
      enableAutoCommit: false
      autoCommitInterval: 1600
      offset-reset: latest
      currency: 1
      keyDeserializer: org.apache.kafka.common.serialization.StringDeserializer
      valueDeserializer: org.apache.kafka.common.serialization.StringDeserializer
    # 第二个消费者
    secondConsumer:
      server: 127.0.0.1:9092,127.0.0.2:9092
      groupId: consumerGroup2
      enableAutoCommit: false
      autoCommitInterval: 1600
      offset-reset: latest
      currency: 1
      keyDeserializer: org.apache.kafka.common.serialization.StringDeserializer
      valueDeserializer: org.apache.kafka.common.serialization.StringDeserializer
```

### 使用:

#### 在项目中创建KafkaConfig.java

```java

@Configuration
public class KafkaConfig {

    /**
     * 注入消费者
     */
    @Autowired
    private ConsumerConfig consumerConfig;

    /**
     * 消费者: firstConsumer
     */
    @Bean(KafkaCrewConstant.FIRST_CONSUMER)
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> firstConsumer() {
        return this.consumerConfig.getConsumer(KafkaCrewConstant.FIRST_CONSUMER);
    }

    /**
     * 消费者: secondConsumer
     */
    @Bean(KafkaCrewConstant.FIRST_CONSUMER)
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> secondConsumer() {
        return this.consumerConfig.getConsumer(KafkaCrewConstant.SECOND_CONSUMER);
    }
}
```

#### 业务中使用

```java
/**
 * 第一个消费者消费
 */
@KafkaListener(topics = KafkaConstants.TOPIC_1, containerFactory = KafkaCrewConstant.FIRST_CONSUMER)
public void consumeFirst(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
    // 创建日志TraceId
    SnLog.setBegin();
    try{
        for(ConsumerRecord<String, String> record:records){
        log.info("第一个消费者, key: {}, value: {}",record.key(),record.value());
        }
    } catch (Exception e) {
        // 注销日志TraceId
        SnLog.removeBegin();  
    } finally {
        ack.acknowledge();
        // 注销日志TraceId
        SnLog.removeBegin();
    }
}

/**
 * 第二个消费者消费
 */
@KafkaListener(topics = KafkaConstants.TOPIC_2, containerFactory = KafkaCrewConstant.FIRST_CONSUMER)
public void consumeSecond(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
    // 创建日志TraceId
    SnLog.setBegin();
    try {
        for (ConsumerRecord<String, String> record : records) {
            log.info("第二个消费者, key: {}, value: {}", record.key(), record.value());
        }
    } catch (Exception e) {
        // 注销日志TraceId
        SnLog.removeBegin();
    } finally {
        ack.acknowledge();
        // 注销日志TraceId
        SnLog.removeBegin();
    }
}
```

---

## 注意：

1.当前如果涉及到中间件接口来源数据，是没有`Crew`的信息的，会造成无法使用内部调用，可以用：`UserContext.getUserContext()`的方法获取到可以使用调用其他接口的准入许可。

```java

/**
 * 未从gateway发起的调用
 */
public static IcsUser getContext() {
        if (Objects.isNull(THREAD_LOCAL.get())) {
            String adminToken = (String) UserContext.redisClient.get(RedisConstants.IAM_TOKEN_KEY);
            HeaderContext.setContext(HeaderDto.builder()
                .useAdminToken(Boolean.TRUE)
                .crewToken(CommonConstants.EMPTY)
                .adminToken(adminToken)
                .build());
            THREAD_LOCAL.set(IcsUser.newBuilder()
                .setCrewId(-999)
                .setSelfToken(CommonConstants.EMPTY)
                .setAdminToken(adminToken)
                .setTraceId(MDC.get(TraceConstants.TRACE_ID))
                .addItemIds(0)
                .addTenantIds(0)
            .build());
        }
        return THREAD_LOCAL.get();
}
```

如上所示：`crewId: -999`, `云平台自用TOKEN为空`, `ADMIN的TOKEN有值, 且会默认使用ADMIN的TOKEN, 即调用云平台`。
如果调用了`UserContext.getContext()`, 需要显示调用`UserContext.clearContext()`。