# gRPC服务

`gRPC`是该套框架基本通讯协议，即所有的服务都应该有`gRPC`的服务能力。

## bootstrap.yml

内容：

```yaml
spring:
  application:
    name: test-server
  profiles:
    include: "core,grpc,schedule"
    active: ${APPLICATION_PROFILES:native}
server:
  port: ${HTTP_PORT:15001}
  servlet:
    context-path: /test-server
grpc:
  server:
    port: ${GRPC_PORT:16001}
mybatis:
  type-aliases-package: com.x.team.test.server.entity
```

+ 项目目录路径遵循`com.x.team.${server-name}.server`的方式；
+ 默认启动的环境为`native`(本地)；
+ `spring.profiles.include`中：
  + 必须添加`core`，为基础`nacos`配置启动项，同时新增`graceful shutdown`的方法；
  + 必须添加`grpc`，为基础处理`gRPC`通信协议和相关基础信息的流转；
  + 选择添加`schedule`，即可以提供相关`XXL-JOB`的分布式定时任务方案。

## database

### 数据库配置(`resource`中)：

+ `generator.properties`为提供给`generator.xml`的生成`Mapper`的配置；
+ `generator.xml`是生成相关`entity`、`repository`和`mapper`的配置；

### 重要配置(`pom.xml`)：

```xml
<properties>
    <targetJavaProject>${basedir}/src/main/java</targetJavaProject>
    <targetResourcesProject>${basedir}/src/main/resources</targetResourcesProject>
    <targetModelPackage>com.x.team.test.server.entity</targetModelPackage>
    <targetMapperPackage>com.x.team.test.server.repository</targetMapperPackage>
</properties>
```

### `nacos`配置：

```yaml
spring:
  # 数据库配置
  dataSource:
    username: test
    password: test123
    url: jdbc:mysql://172.17.110.106:3388/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&useSSL=false&nullCatalogMeansCurrent=true
```

## redis

### `nacos`配置：

```yaml
# redis配置
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: xxyyzz
    database: 0
```

## 项目目录：

项目中禁止引用`*`的方式进行类的`import`。

### entity

数据库实体类，可由`maven`的插件生成；

生成后尽量删除相关的`getter/setter`，使用`@lombok`替代；

### repository

数据库方法接口，可由`maven`的插件生成；

需要在相关方法接口类上添加`@Repository`的注解，符合`spring`的定义规范。

### mapper

数据库`ORM`的`SQL`配置`xml`文件，可由`maven`的插件生成；

### grpc

用来继承相关`protoc`生成的`gRPC`接口，并实现相关接口方法。

### service

定义`gRPC`的分层设计，实现相关业务逻辑的接口类，包括`redis`、`mysql`等业务。

### service.impl

实现`service`中的接口，并可以在`grpc`中，通过`@Autowired`方式注入。

## gRPC内部调用方式

### 注入：

```java
@GrpcClient(CloudServerConstants.CLIENT_TEST_SERVER)
private TestApiGrpc.TestApiBlockingStub testApiGrpc;
```

**其中：**

`CloudServerConstants.java`中，可以定义所有微服务的调用服务名称。

在`NACOS`中添加`gRPC`服务的配置：

```yaml
# GRPC连接池
grpc:
  client:
    # test服务
    test-server:
      address: dns:/127.0.0.1:16001
    # api服务
    api-server:
      address: dns:/127.0.0.1:16002
    # foo服务
    foo-server:
      address: dns:/127.0.0.1:16003
```

对应的`CloudServerConstants.java`:

```java
/**
 * 描述：云服务的常量池
 *
 * @author MassAdobe
 * @date Created in 2023/1/28 17:26
 */
public class CloudServerConstants {
    /**
     * test-server-grpc-client
     */
    public final static String CLIENT_TEST_SERVER = "test-server";
    /**
     * api-server-grpc-client
     */
    public final static String CLIENT_API_SERVER = "api-server";
    /**
     * foo-server-grpc-client
     */
    public final static String CLIENT_FOO_SERVER = "foo-server";
}

```

### 调用：

```java
TestResponse testResponse = this.testApiGrpc.testGet(TestRequest.newBuilder()
        .setFirstId("inner call first")
        .build());
```