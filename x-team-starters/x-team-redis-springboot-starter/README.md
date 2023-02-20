# x-team-redis-sprintboot-starter

## redis-client

+ 遵循`org.springframework.data.redis.core.RedisTemplate`的协议；
+ 处理数据类型的工具类

## redis-session-client

+ 遵循`org.redisson.api.RedissonClient`的协议；
+ 处理锁一类的问题

## nacos中配置:
```yaml
# redis配置
spring:
  redis:
    port: 6379
    host: redis.base
    password: 86zsEp
    database: 2
```