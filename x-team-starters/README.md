# x-team-starters

自研的`springboot`自动装载类，提高框架的易用性；

---

## x-team-config-springboot-starter

基础配置自动装载

### 服务配置装载类，涉及到：

+ `springboot-configuration`；
+ `springboot-auto-configuration`；
+ 多环境配置读取、装载、分发；
+ `NACOS`配置中心，动态配置；
+ 自定义启动`banner`；
+ `springboot`基本配置；
+ `spring`关于`Bean`的工具类；
+ `NACOS`启动工具类；
+ 调用`GRPC`服务的服务常量池；
+ 健康探针服务；
    + `http://${ip}:${port}/${server-name}/warmup`
    + 示例：`http://127.0.0.1:8080/test-server/warmup`
+ 日志级别热改动接口；

### 优先启动项：

`bootstrap.yml`只配置相关`NACOS`信息，隔离开发改动，保证环境的安全性。 环境分为：

+ `native`：本地；
    + `file-location`是日志地址，本地默认：`/usr/local/logs/x-team`；
+ `dev`：开发；
    + `file-location`是日志地址，开发默认：`/usr/local/logs/x-team`；
+ `staging`：测试；
    + 启动时需要把`NACOS_CONFIG_ADDR`、`NACOS_CONFIG_NAMESPACE`、`NACOS_CONFIG_USERNAME`、`NACOS_CONFIG_PASSWORD`放在启动项塞入；
    + `file-location`是日志地址，测试默认：`/var/log`；
+ `uat`：预发布；
    + 启动时需要把`NACOS_CONFIG_ADDR`、`NACOS_CONFIG_NAMESPACE`、`NACOS_CONFIG_USERNAME`、`NACOS_CONFIG_PASSWORD`放在启动项塞入；
    + `file-location`是日志地址，测试默认：`/var/log`；
+ `prod`：生产；
    + 启动时需要把`NACOS_CONFIG_ADDR`、`NACOS_CONFIG_NAMESPACE`、`NACOS_CONFIG_USERNAME`、`NACOS_CONFIG_PASSWORD`放在启动项塞入；
    + `file-location`是日志地址，测试默认：`/var/log`；

#### 配置模板：

##### 配置中心配置：

```yaml
spring:
  cloud:
    nacos:
      config:
        extension-configs[0]:
          # 路由配置，GRPC服务间调用
          data-id: route.yml
          group: X-TEAM

---
spring:
  profiles: native
  cloud:
    # nacos 配置
    nacos:
      config:
        server-addr: ${NACOS_CONFIG_ADDR:127.0.0.1:8848}
        file-extension: 'yml'
        namespace: ${NACOS_CONFIG_NAMESPACE:x-team}
        username: ${NACOS_CONFIG_USERNAME:gaussian}
        password: ${NACOS_CONFIG_PASSWORD:gaussian}
logging:
  file-location: /usr/local/logs/x-team
```

---

## x-team-database-springboot-starter

数据库自动装载

+ 设置基础连接池、超时时间等配置；
+ 数据库连接配置需要在`NACOS`中配置；
+ 如果是私有化部署，使用`MyCat`做`mysql`读写分离的`proxy`；
+ 如果是`SaaS`部署，直接使用`Aliyun`的`proxy`做读写分离，无需在代码中声明式给出读写分离；
+ `tk-mybatis`的`mapper`位置；
+ `pageHelper`的配置。

#### 配置模板：

每个服务可以独立配置。

##### 配置中心配置：

```yaml
spring:
  # 数据库配置
  dataSource:
    username: test
    password: test123
    url: jdbc:mysql://172.17.110.106:3388/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&useSSL=false&nullCatalogMeansCurrent=true
```

---

## x-team-dingtalk-springboot-starter

钉钉群通知自动装配

### 步骤：
+ 群设置；
+ 只能群助手；
+ 添加机器人；
+ 填入：机器人名字，设置安全设置为“加签”。

### 接口调用：

在接口调用中：SendDingTalkDto：

```java
    /**
     * 相关accessToken
     */
    private String accessToken;
    /**
     * 秘钥(钉钉群秘钥)
     */
    private String dingTalkSecret;
    /**
     * 发送消息内容
     */
    private String messageText;
    /**
     * 是否需要At所有人
     */
    private Boolean needAtAll = Boolean.FALSE;
```

其中：

### accessToken

为完成安全设置后，复制出机器人的Webhook地址，可用于向这个群发送消息的URL中存在access_token：
例如： https://oapi.dingtalk.com/robot/send?access_token=XXXXXX

测试：access_token = `f06732a69531bf9d543f36772ce33327424fbab3cf0c0099ec6dd3652e4df3fe`

### dingTalkSecret

为加签后的以“SEC”为开头的签名。

测试：dingTalkSecret = `SECcad814dee64e929aeb1aa280148dc617e16a6231812a270feee1c85a7adf78b5`

### messageText

发送内容文本。

### needAtAll

是否需要通知所有人(@所有人)。

---

## x-team-grpc-springboot-starter

`GRPC`自动装载

+ `GRPC-CLIENT`的拦截器；
+ `GRPC-SERVER`的拦截器；
+ `GRPC`的`EXCEPTION`的声明；
    + 遵循`GOOGLE`的`ERROR-INFO`规范；
+ `GRPC`的`EXCEPTION`全局拦截；
+ `GRPC-PROTO`转`JAVA-POJO`的工具类；
+ `JAVA-POJO`转`GRPC-PROTO`的工具类；
+ `GRPC`的全局调用、通讯配置；
+ `GRPC`的全局安全配置；
+ 用户信息的获取、透传、销毁；
+ `TraceId`的日志追踪功能，添加和销毁；
+ RSA双向加解密；
+ 支持`GRPC`的`pod to pod`、`pod to stream`、`stream to pod`、`stream to stream`通讯方式；

#### 注意：

在`GRPC`的服务中，`EXCEPTION`处理如下：

```java
throw new GrpcException(ErrorCodeMsg.SERVER_ERROR);
```

### `NACOS`配置:

```yaml
# 秘钥配置
rsa:
  token:
    key:
      # 公钥
      public-rsa: 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDhbP0Ky7W/SPwqKV3tX4Y7AqWpZJJkozuiEEG0SVab93XiVZxSUT0BXdnjV0CovLEJ+LQUglQjHM3pT1AcOPH9YixprKaf5SiGzXR9fmrQFpO33Oo4bNkftq4zSz7a+goLnX/7pDLW14g/uUOS79XIHrVwr2c2FtJzJeb7sbOAwIDAQAB'
      # 私钥
      private-rsa: 'MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMOFs/QrLtb9I/CopXe1fhjsCpalkkmSjO6IQQbRJVpv3deJVnFJRPQFd2eNXQKi8sQn4tBSCVCMczelPUBw48f1iLGmspp/lKIbNdH1+atAWk7fc6jhs2R+2rjNLPtr6Cgudf/ukMtbXiD+5Q5Lv1cgetXCvZzYW0nMl5vuxs4DAgMBAAECgYAZqcD2+XwKZ+uBuQEO91l3t/Oj1lOfKIcq/+NZG7aFak157uLTt8Q/K0CC7QEaSX0H1hxlL39dfTHaSewCO08KQyrrCyEKttApk+nEkPKAXIkNaJILxVankd75AeXJ9agjcfh56yuhi5vRztYhdHd+4l279e68qhkTmMkw6BFaIQJBAN/M+6JtYIWHVMYg2cRwKwom9MVlxJHJOHgzYioU5r8chHQSrdXfdqlABo8giBmsc6B4Aboo3YZQZ1Or7XPpbqsCQQDfpyuCgxLQ9NPJwzucDEoER6Z6fp1SZuarGBrK49NWXjUEBJEFTV+rltRNINUpiLM7ix8os575x9BDVAf2Tb4JAkBy85i0XzXaoB5c/facvXh96HCFC7ahoxdMNihPsRisRPuLmySK3BLY8DfuQCln9noVjE5mziKqoJXSFlcK2HWtAkEAqG0yHQUshGP7onP+ThQLIZH70z/KpiWMDMkIVEah/ubPMglmVQHhzEy38KWLIc4qe+rXOkWasSxCsJF1tYzyUQJAKt1tvjrFWReIZmJHQAZ6d1ec3I6WYOhuEMrNrSJ8p3MSXWpmaMD0X9ehsQcgLrhFbe3Yc6YvryQLbWkoYyD/DA=='
```

### 解析(decode)

```java
@Autowired
private RSAConfig rsaConfig;

// ......
// 解析token
String tokenInfo;
try {
    tokenInfo = EncryptUtils.serverDecrypt(token, this.rsaConfig.getPrivateRsa());
} catch (Exception e) {
    log.error("[rsa-decode]: token decrypt error，error：{}", e.getMessage());
    throw new GrpcException(ErrorCodeMsg.TOKEN_ERROR);
}
// ......
```

### 加密(encode)

```java
@Autowired
private RSAConfig rsaConfig;

// ......
// 加密token
String token;
try {
    token = EncryptUtils.serverEncrypt(this.rsaConfig.getPublicRsa(), tokenInfo);
} catch (Exception e) {
    log.error("[加密]: token encrypt error，error：{}", e.getMessage());
    throw new GrpcException(ErrorCodeMsg.TOKEN_ERROR);
}
// ......
```

+ 直接抛出异常，框架会处理`GRPC`至`GRPC`内部调用的传输，同时也会处理`GRPC`至`REST`的转换；
+ ~~如不严格抛出`GrpcException`，而抛出`RestException`，会造成系统不可用。~~

---

## x-team-http-springboot-starter

`HTTP`调用池、工具类自动装载

+ `HTTP`的包裹(请求-响应)日志；
+ `HTTP`的客户端装配至`springboot`中，方便`@Autowired`；
+ `OkHttp`配置，连接池等；
+ `HTTP`调用灵活使用是否带上`Header`等操作；
+ 目前提供：`GET`、`POST`、`PUT`、`DELETE`的`Restful`调用方法；
+ 提供`HTTPS`的调用内置配置，开箱即用；

### 方法：

```java
/**
 * get request with header
 *
 * @param url     request url path
 * @param headers request headers {k1, v1 k2, v2, ...}
 * @return string 返回值
 */
public String doGet(String url,Map<String, String> headers);

/**
 * get request with header and return with header
 *
 * @param url     request url path
 * @param headers request headers {k1, v1 k2, v2, ...}
 * @return PostWithHeaderDto 返回值
 */
public PostWithHeaderDto doGetReturnHeaders(String url,Map<String, String> headers);

/**
 * get request with params and header
 *
 * @param url     request url path
 * @param params  request params map
 * @param headers request headers {k1, v1 k2, v2, ...}
 * @return string
 */
public String doGet(String url,Map<String, String> params,Map<String, String> headers);

/**
 * post request with param and header in json
 *
 * @param url    request url address
 * @param params request headers map
 * @return string
 */
public String doPostJson(String url,String params,Map<String, String> headers);

/**
 * post request with param and header and return with header in json
 *
 * @param url    request url address
 * @param params request headers map
 * @return PostWithHeaderDto
 */
public PostWithHeaderDto doPostJsonReturnHeaders(String url,String params,Map<String, String> headers);

/**
 * put request with param and header in json
 *
 * @param url    request url address
 * @param params request headers map
 * @return string
 */
public String doPutJson(String url,String params,Map<String, String> headers);

/**
 * delete request with param and header in json
 *
 * @param url    request url address
 * @param params request headers map
 * @return string
 */
public String doDeleteJson(String url,String params,Map<String, String> headers);

/**
 * post request with param and header in xml
 *
 * @param url request url address
 * @param xml request data, xml string
 * @return string
 */
public String doPostXml(String url,String xml,Map<String, String> headers);
```

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

## x-team-kafka-springboot-starter

`kafka`自动装配类

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

#### 在项目中创建`KafkaConfig.java`

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

1.当前如果涉及到中间件接口来源数据，是没有`User`的信息的，会造成无法使用内部调用，可以用：`HeaderContext.getUserContext()`的方法获取到可以使用调用其他接口的准入许可。

2.如果调用了`HeaderContext.getContext()`, 需要显示调用`HeaderContext.clearContext()`。

---

## x-team-log-springboot-starter

日志自动装配

+ 日志配置，包括控制台输出和日志文件输出；
+ 日志文件的`rolling`配置；
+ 日志的注解使用方式；
+ 使用`logback`的文件配置方式，且日志输出和打印标准化；
+ 日志添加`TraceId`方便链路追踪；
+ 提供无源调用的`TraceId`声明式生成和销毁方法；
+ 支持日志级别：
    + `trace`：是追踪，就是程序推进以下，你就可以写个trace输出，所以trace应该会特别多，不过没关系，我们可以设置最低日志级别不让他输出。
    + `debug`：指出细粒度信息事件对调试应用程序是非常有帮助的。
    + `info`：消息在粗粒度级别上突出强调应用程序的运行过程。
    + `warn`：输出警告及warn以下级别的日志。
    + `error`：输出错误信息日志。
    + `off`：表示关闭全部日志。
    + `all`：表示开启全部日志。
+ 支持动态调节日志级别：
    + `PUT`请求：`http://${ip}:${port}/${server-name}/logLevel`；
    + 入参：`level`：
        + `trace`；
        + `debug`；
        + `info`；
        + `warm`；
        + `error`；
        + `off`；
        + `on`；
    + 示例：`http://127.0.0.1:8080/test-server/logLevel?level=debug`

### 方法：

#### 在`java`类上加入：`@Slf4j`：

```java
@Slf4j
@Service
public class LogLevelImpl implements LogLevelService {

}
```

#### 使用方式：

`[${rest-interceptor}]`用于未来`logtail`的分词。

```java
log.debug("[rest-interceptor]: 当前请求: {}, 该请求毋需TOKEN验证",request.getRequestURI());
log.info("[rest-interceptor]: 当前请求: {}, 该请求毋需TOKEN验证",request.getRequestURI());
log.error("[rest-interceptor]: 当前请求: {}, 该请求毋需TOKEN验证",request.getRequestURI());
```

---

## x-team-oss-springboot-starter

OSS对象存储自动装配

+ 自动化配置；
+ 提供公有和私有独立方法；

### `NACOS`配置：

在`x-team-config-springboot-starter`中的`bootstrap.yml`中添加：

```yaml
spring:
  cloud:
    nacos:
      config:
        extension-configs[0]:
          data-id: route.yml
          group: X-TEAM
        extension-configs[1]:
          data-id: oss.yml
          group: X-TEAM
```

`NACOS`中添加配置：

```yaml
oss:
  # 公有配置
  public:
    endpoint: oss-cn-shanghai.aliyuncs.com
    access-key-id: LTAI5tFD55ymDeNFXsM92b
    access-key-secret: vUMqeDytWb6mJqrrrUYVgFJDvf6t
    bucket-name: aiot-bu-public
    permit-format:
      - apk
  # 私有配置
  private:
    endpoint: oss-cn-shanghai.aliyuncs.com
    access-key-id: LTAI5tFD55ymDeNFXsM92b
    access-key-secret: vUMqeDytWb6mJqrrrUYVgFJDvf6t
    bucket-name: aiot-bu-private
    region-id: cn-shanghai
    role-arn: 'acs:ram::1094210804279118:role/aiot-oss-sts'
    role-session-name: 'ics'
    policy: '{"Version": "1", "Statement": [{"Action": ["oss:PutObject","oss:GetObject"], "Resource": ["acs:oss:*:1094210804279118:aiot-bu-private/*","acs:oss:*:1094210804279118:aiot-bu-public/*"],"Effect": "Allow"}]}'
    permit-format:
      - apk
```

---

## x-team-redis-springboot-starter

`Redis`自动装配

+ 遵循`org.springframework.data.redis.core.RedisTemplate`的协议；
    + 处理数据类型的工具类；
    + 具体方法在`com.x.team.redis.springboot.starter.client.RedisClient`中；
+ 遵循`org.redisson.api.RedissonClient`的协议；
    + 处理锁一类的问题；
    + 具体方法在`com.x.team.redis.springboot.starter.client.RedisSessionClient`中；
+ 连接池配置；
+ `Redis`异常；
+ 序列化和反序列化配置；

### 配置：

每个服务可以独立配置。

```yaml
spring:
  # redis配置
  redis:
    port: 6379
    password: 86zsEp
    host: 172.10.4.91
    database: 10
```

### 方法：

#### 前提：

所有的`REDIS-KEY`和`REDIS-EXPIRE-TIME`需要声明在`x-team-common`中的`RedisConstants`常量池中，方便统一管理，不允许系统出现魔法值。

#### 数据类型工具类：

```java
@AutoWired
private RedisClient redisClient;

public void test() {
    String answer = (String) this.redisClient.get(RedisConstants.KEY);
}
```

#### 数据类型工具类：

```java
@AutoWired
private RedisSessionClient redisSessionClient;

public void test() {
    // 上锁
    RLock rLock = this.redisSessionClient.lock(RedisConstants.KEY, TimeUnit.SECONDS, RedisConstants.KEY_EXPIRE_TIME);
    // 解锁
    rLock.unlock();
}
```

---

## x-team-rest-springboot-starter

服务添加`restful`接口能力自动装配

+ 包裹日志输出(请求和响应)日志；
+ 调用链错误处理；
+ 返回调用客户端中给出`TraceId`用来做日志链路查询；
+ 优雅退出；
+ 跨域；
+ 请求头设置，允许`GET`、`POST`、`PUT`、`DELETE`的`RestFul`请求；
+ 返回结构体统一处理；
+ `REST`的`EXCEPTION`的声明；
+ `REST`的`EXCEPTION`全局拦截；
+ `GRPC`的`EXCEPTION`处理；
+ 抛出错误的前端结构体返回全局处理；
+ 权限、令牌校验拦截器；

#### 注意：

在`REST`的服务中，`EXCEPTION`处理如下：

```java
throw new RestException(ErrorCodeMsg.SERVER_ERROR);
```

+ 直接抛出异常，框架会处理`REST`至`REST`内部调用的传输，同时也会处理`GRPC`至`REST`的转换；
+ 如不严格抛出`RestException`，而抛出`GrpcException`，会造成系统不可用。

### 方法：
```java
package com.x.team.test.rest.controller;

/**
 * 描述：测试restful接口类
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 14:31
 */
@Slf4j
@RestController
@RequestMapping("/v1/test")
public class TestController {

    @GrpcClient(GrpcServerConstants.TEST_GRPC_SERVER)
    private TestGrpc.TestBlockingStub testBlockingStub;

    /**
     * 测试服务GET
     */
    @GetMapping("/testingGet/{first}")
    public Resp<TestResponse> testingGet(
            @PathVariable(name = "first", value = "first", required = true) String first,
            @RequestParam(name = "second", value = "second", required = false, defaultValue = "true") Boolean second
    ) {
        TestResponse testResponse = this.testBlockingStub.testing(TestRequest.newBuilder()
                .setFirst(first)
                .setSecond(second)
                .build());
        // 获取用户信息
        UserContext context = HeaderContext.getContext();
        log.info("[测试服务GET]: {}", context);
        return Resp.success(testResponse);
    }
    
    /**
     * 测试服务POST
     */
    @PostMapping("/testingPost")
    public Resp<TestResponse> testingPost(@RequestBody TestRequestVo testRequestVo) {
        // http-object to grpc-proto
        TestResponse testResponse = this.testBlockingStub.testing(
                Objects.requireNonNull(TransportUtils.toProto(TestRequest.newBuilder(), testRequestVo))
                        .build());
        // 获取用户信息
        UserContext context = HeaderContext.getContext();
        log.info("[测试服务POST]: {}", context);
        return Resp.success(testResponse);
    }
    
    /**
     * 测试服务PUT
     */
    @PutMapping("/testingPut/{first}")
    public Resp<TestResponse> testingPut(
            @PathVariable(name = "first", value = "first", required = true) String first,
            @RequestBody TestRequestVo testRequestVo
    ) {
        testRequestVo.setFirst(first);
        // http-object to grpc-proto
        TestResponse testResponse = this.testBlockingStub.testing(
                Objects.requireNonNull(TransportUtils.toProto(TestRequest.newBuilder(), testRequestVo))
                        .build());
        // 获取用户信息
        UserContext context = HeaderContext.getContext();
        log.info("[测试服务PUT]: {}", context);
        return Resp.success(testResponse);
    }

    /**
     * 测试服务DELETE
     */
    @DeleteMapping("/testingDelete/{first}")
    public Resp<TestResponse> testingDelete(
            @PathVariable(name = "first", value = "first", required = true) String first,
            @RequestBody TestRequestVo testRequestVo
    ) {
        testRequestVo.setFirst(first);
        // http-object to grpc-proto
        TestResponse testResponse = this.testBlockingStub.testing(
                Objects.requireNonNull(TransportUtils.toProto(TestRequest.newBuilder(), testRequestVo))
                        .build());
        // 获取用户信息
        UserContext context = HeaderContext.getContext();
        log.info("[测试服务DELETE]: {}", context);
        return Resp.success(testResponse);
    }
}

```

---

## x-team-schedule-springboot-starter

定时任务自动装配

+ 适配`XXL-JOB`分布式定时任务；
+ 打印日志；
+ 添加`traceId`和`stepId`方便链路追踪。

### `NACOS`配置：



---

## x-team-sms-springboot-starter

短信自动装配

+ 发送短信；
+ 云片；
+ 阿里云；

### `NACOS`配置：

在`x-team-config-springboot-starter`中的`bootstrap.yml`中添加：

```yaml
spring:
  cloud:
    nacos:
      config:
        extension-configs[0]:
          data-id: route.yml
          group: X-TEAM
        extension-configs[1]:
          data-id: oss.yml
          group: X-TEAM
        extension-configs[2]:
          data-id: sms.yml
          group: X-TEAM
```

`NACOS`中添加配置：

```yaml
sms:
  region-id: cn-shanghai
  access-key-id: LTAI5t71A66RMDammshhYe
  access-key-secret: noqqM1RMnEScL7RkRXUCcJWDxcvJ
```

### 方法：

```java
@AutoWired
private SmsService smsService;
```

---

## x-team-wechat-springboot-starter

微信自动装配

+ `NACOS`动态配置`APP-ID`和`APP-SECRET`；
+ 微信的`SESSION-KEY`动态获取；
+ 微信异常申明；
+ 获取用户信息；

### 方法：

```java
/**
 * 描述：微型小程序验证
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:37
 */
public interface WechatAuthService {

    /**
     * 获取接口调用凭证
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
     */
    WechatAccessTokenRespVo wechatAccessToken();

    /**
     * 获取微信用户的openId
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
     *
     * @param jsCode 登录时获取的 code
     */
    WechatOpenIdRespVo wechatOpenId(String jsCode);

    /**
     * 获取微信用户的手机号码
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/phonenumber/phonenumber.getPhoneNumber.html
     */
    WechatPhoneNumRespVo wechatPhoneNum(WechatPhoneNumReqVo wechatPhoneNumRequestVo);

    /**
     * 获取微信用户的用户信息
     * <p>
     * https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html
     */
    WechatUserInfoRespVo wechatUserInfo(WechatUserInfoReqVo wechatUserInfoRequestVo);
}
```


