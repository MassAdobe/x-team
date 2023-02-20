package com.x.team.redis.springboot.starter.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.x.team.common.constants.CommonConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.redis.springboot.starter.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 描述：RedisTemplate工具类：针对所有的hash都是以h开头的方法，针对所有的Set都是以s开头的方法(不含通用方法)，针对所有的List都是以l开头的方法
 * <p>
 * * RedisTemplate工具类：
 * * 针对所有的hash都是以h开头的方法
 * * 针对所有的Set都是以s开头的方法(不含通用方法)
 * * 针对所有的List都是以l开头的方法
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:20
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class RedisClient {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        RedisClient.OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 描述：ping Redis
     *
     * @author MassAdobe
     * @date Created in 2023/1/20 16:20
     */
    public String ping() {
        Object rtn = this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ping();
            }
        });
        if (null != rtn) {
            return rtn.toString();
        }
        return null;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public void expire(String key, long time) {
        try {
            if (time > 0) {
                log.debug("【REDIS】：EXEC EXPIRE; KEY:{}, TIME: {}}", key, time);
                this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        try {
            log.debug("【REDIS】：EXEC GET EXPIRE; KEY:{}", key);
            return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-GET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            log.debug("【REDIS】：EXEC HASKEY; KEY:{}", key);
            return this.redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-HASKEY】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(Object... key) {
        if (null != key && key.length > 0) {
            if (key.length == 1) {
                log.debug("【REDIS】：EXEC DEL; KEY:{}", key[0]);
                this.redisTemplate.delete(String.valueOf(key[0]));
            } else {
                log.debug("【REDIS】：EXEC DEL; KEY:{}", CollectionUtils.arrayToList(key).toString());
                this.redisTemplate.delete(CollectionUtils.arrayToList(key).stream().map(String::valueOf).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public <T> void dels(List<T> keys) {
        if (null != keys && keys.size() > 0) {
            List<String> key = keys.stream().map(String::valueOf).collect(Collectors.toList());
            log.debug("【REDIS】：EXEC DELS; KEYS:{}", key);
            this.redisTemplate.delete(key);
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        log.debug("【REDIS】：EXEC GET; KEY:{}", key);
        return key == null ? null : this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            log.debug("【REDIS】：EXEC SET; KEY:{}, VALUE:{}}", key, value);
            this.redisTemplate.opsForValue().set(key, String.valueOf(value));
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    public boolean setNx(String key, Object value) {
        try {
            log.debug("【REDIS】：EXEC SET; KEY:{}, VALUE:{}}", key, value);
            return this.redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value));
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean setNx(String key, Object value, long time) {
        try {
            log.debug("【REDIS】：EXEC SET EXPIRE; KEY:{}, VALUE:{}, TIME:{}", key, value, time);
            if (time > 0) {
                return this.redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value), time, TimeUnit.SECONDS);
            } else {
                return setNx(key, String.valueOf(value));
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-SET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public void set(String key, Object value, long time) {
        try {
            log.debug("【REDIS】：EXEC SET EXPIRE; KEY:{}, VALUE:{}, TIME:{}", key, value, time);
            if (time > 0) {
                this.redisTemplate.opsForValue().set(key, String.valueOf(value), time, TimeUnit.SECONDS);
            } else {
                set(key, String.valueOf(value));
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-SET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            log.error("【REDIS-CLIENT-INCR】：{}", ErrorCodeMsg.REDIS_INCR_ERROR.getZhMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
        log.debug("【REDIS】：EXEC INCR; KEY:{}, DELTA-TIME:{}", key, String.valueOf(delta));
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            log.error("【REDIS-CLIENT-DECR】：{}", ErrorCodeMsg.REDIS_INCR_ERROR.getZhMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
        log.debug("【REDIS】：EXEC DECR; KEY:{}, DELTA-TIME:{}", key, delta);
        return this.redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, Object item) {
        log.debug("【REDIS】：EXEC H-GET; KEY:{}, SUB-KEY:{}", key, item);
        return this.redisTemplate.opsForHash().get(key, String.valueOf(item));
    }

    /**
     * 获取hashKey对应的所有键值
     */
    public <T> List<T> hmget(String mainKey, List<Object> keys) {
        log.debug("【REDIS】：EXEC H-MGET; MAIN-KEY:{}, KEYS:{}", mainKey, keys.toString());
        return this.redisTemplate.opsForHash().multiGet(mainKey, keys.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public void hmset(String key, Map<Object, Object> map) {
        try {
            log.debug("【REDIS】：EXEC H-MSET; KEY:{}, VALUE:{}", key, map.toString());
            Map<String, String> pres = new HashMap<>(map.size());
            map.forEach((k, v) -> pres.put(String.valueOf(k), String.valueOf(v)));
            this.redisTemplate.opsForHash().putAll(key, pres);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-H-MSET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public void hmsetMapList(String key, Map<String, List<Long>> map) {
        try {
            Map<String, String> mapping = new HashMap<>(map.size());
            for (String k : map.keySet()) {
                String str = RedisClient.OBJECT_MAPPER.writeValueAsString(map.get(k));
                mapping.put(k, str);
            }
            log.debug("【REDIS】：EXEC H-MSET; KEY:{}, VALUE:{}", key, mapping.toString());
            this.redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-H-MSET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     */
    public void hmset(String key, Map<String, String> map, long time) {
        try {
            log.debug("【REDIS】：EXEC H-MSET EXPIRE; KEY:{}, VALUE:{}, TIME:{}", key, map.toString(), String.valueOf(time));
            this.redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-H-MSET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     */
    public void hset(Object key, Object item, Object value) {
        try {
            log.debug("【REDIS】：EXEC H-SET; KEY:{}, SUB-KEY:{}, VALUE:{}", key.toString(), item.toString(), value.toString());
            this.redisTemplate.opsForHash().put(String.valueOf(key), String.valueOf(item), String.valueOf(value));
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-H-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     */
    public void hset(Object key, Object item, Object value, long time) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC H-SET EXPIRE; KEY:{}, SUB-KEY:{}, VALUE:{}, TIME:{}", key, item, val, String.valueOf(time));
            this.redisTemplate.opsForHash().put(String.valueOf(key), String.valueOf(item), String.valueOf(value));
            if (time > 0) {
                expire(String.valueOf(key), time);
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-H-SET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(Object key, Object... item) {
        log.debug("【REDIS】：EXEC H-DEL; KEY:{}, SUB-KEYS:{}", key, item);
        this.redisTemplate.opsForHash().delete(String.valueOf(key), Arrays.stream(item).map(String::valueOf).toArray());
    }

    /**
     * 删除hash表中的值
     *
     * @param key 键 不能为null
     */
    public void hdel(Object key) {
        log.debug("【REDIS】：EXEC H-DEL; KEY:{}", key);
        this.redisTemplate.delete(String.valueOf(key));
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, Object item) {
        log.debug("【REDIS】：EXEC H-HASKEY; KEY:{}, SUB-KEYS:{}", key, item);
        return this.redisTemplate.opsForHash().hasKey(key, String.valueOf(item));
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        log.debug("【REDIS】：EXEC H-INCR; KEY:{}, SUB-KEYS:{}, BY:{}", key, item, String.valueOf(by));
        return this.redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        log.debug("【REDIS】：EXEC H-DECR; KEY:{}, SUB-KEYS:{}, BY:{}", key, item, -by);
        return this.redisTemplate.opsForHash().increment(key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public <T> Set<T> sGet(String key) {
        try {
            log.debug("【REDIS】：EXEC S-GET; KEY:{}", key);
            return this.redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-S-GET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     */
    public boolean sHasKey(String key, Object value) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC S-HASKEY; KEY:{}, VALUE:{}", key, val);
            return this.redisTemplate.opsForSet().isMember(key, String.valueOf(value));
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-S-HASKEY】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 从set中随机返回一个元素
     *
     * @param key 键
     * @return 返回元素
     */
    public Object sPopSingle(String key) {
        try {
            log.debug("【REDIS】：EXEC SET-POP; KEY:{}", key);
            Object pop = this.redisTemplate.opsForSet().pop(key);
            log.debug("【REDIS】：EXEC SET-POP; KEY:{}, VALUE: {}", key, pop);
            return pop;
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-SET-POP】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     */
    public void sSet(String key, Object... values) {
        try {
            log.debug("【REDIS】：EXEC S-SET; KEY:{}, VALUES:{}", key, CollectionUtils.arrayToList(values).toString());
            this.redisTemplate.opsForSet().add(key, Arrays.stream(values).map(String::valueOf).toArray());
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-S-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     */
    public void sSetAndTime(String key, long time, Object... values) {
        try {
            log.debug("【REDIS】：EXEC S-SET EXPIRE; KEY:{}, VALUES:{}, TIME:{}", key, CollectionUtils.arrayToList(values).toString(), time);
            this.redisTemplate.opsForSet().add(key, Arrays.stream(values).map(String::valueOf).toArray());
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-S-SET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            log.debug("【REDIS】：EXEC S-SIZE; KEY:{}", key);
            return this.redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-S-SIZE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     */
    public void setRemove(String key, Object... values) {
        try {
            log.debug("【REDIS】：EXEC S-REMOVE; KEY:{}, VALUES:{}", key, CollectionUtils.arrayToList(values).toString());
            this.redisTemplate.opsForSet().remove(key, Arrays.stream(values).map(String::valueOf).toArray());
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-S-REMOVE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            log.debug("【REDIS】：EXEC L-GET; KEY:{}, START:{}, END:{}", key, start, end);
            return this.redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-GET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            log.debug("【REDIS】：EXEC L-GET-SIZE; KEY:{}", key);
            return this.redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-GET-SIZE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            log.debug("【REDIS】：EXEC L-GET-IDX; KEY:{}, INDEX:{}", key, index);
            return this.redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-GET-IDX】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public void lSet(String key, Object value) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC L-SET; KEY:{}, VALUE:{}", key, val);
            this.redisTemplate.opsForList().rightPush(key, val);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public Long lSet(String key, String value) {
        try {
            log.debug("【REDIS】：EXEC L-SET; KEY:{}, VALUE:{}", key, value);
            return this.redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public void lSet(String key, Object value, long time) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC L-SET EXPIRE; KEY:{}, VALUE:{}, TIME:{}", key, val, time);
            this.redisTemplate.opsForList().rightPush(key, val);
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-SET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public void lSet(String key, List<Object> value) {
        try {
            log.debug("【REDIS】：EXEC L-SET; KEY:{}, VALUES:{}", key, CollectionUtils.arrayToList(value).toString());
            this.redisTemplate.opsForList().rightPushAll(key, value.stream().map(String::valueOf).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-SET】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public void lSet(String key, List<Object> value, long time) {
        try {
            log.debug("【REDIS】：EXEC L-SET EXPIRE; KEY:{}, VALUES:{}, TIME:{}", key, CollectionUtils.arrayToList(value).toString(), time);
            this.redisTemplate.opsForList().rightPushAll(key, value.stream().map(String::valueOf).collect(Collectors.toList()));
            if (time > 0) {
                expire(key, time);
            }
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-SET-EXPIRE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void lUpdateIndex(String key, long index, Object value) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC L-UPDATE-IDX; KEY:{}, INDEX:{}, VALUE:{}", key, String.valueOf(index), val);
            this.redisTemplate.opsForList().set(key, index, val);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-UPDATE-IDX】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     */
    public void lRemove(String key, long count, Object value) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC L-REMOVE; KEY:{}, COUNT:{}, VALUE:{}", key, count, val);
            this.redisTemplate.opsForList().remove(key, count, val);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-REMOVE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 移除并返回列表 key 的尾元素
     *
     * @param key   键
     * @param value 消息体
     */
    public void lPushSingle(String key, Object value) {
        try {
            String val = RedisClient.OBJECT_MAPPER.writeValueAsString(value);
            log.debug("【REDIS】：EXEC L-PUSH-SINGLE; KEY:{}, VALUE: {}", key, val);
            this.redisTemplate.opsForList().leftPush(key, val);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-PUSH-SINGLE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 移除并返回列表 key 的尾元素
     *
     * @param key 键
     * @return 弹出结构体
     */
    public Object lPopSingle(String key) {
        try {
            log.debug("【REDIS】：EXEC L-POP-SINGLE; KEY:{}", key);
            Object obj = this.redisTemplate.opsForList().leftPop(key);
            log.debug("【REDIS】：EXEC L-POP-SINGLE; KEY:{}, VALUE: {}",
                    key, Objects.nonNull(obj) ? obj.toString() : CommonConstants.EMPTY);
            return obj;
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-POP-SINGLE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 移除并返回列表 key 的尾元素
     *
     * @param key 键
     * @return 弹出结构体
     */
    public Object rPopSingle(String key) {
        try {
            log.debug("【REDIS】：EXEC R-POP-SINGLE; KEY:{}", key);
            Object obj = this.redisTemplate.opsForList().rightPop(key);
            log.debug("【REDIS】：EXEC R-POP-SINGLE; KEY:{}, VALUE: {}",
                    key, Objects.nonNull(obj) ? obj.toString() : CommonConstants.EMPTY);
            return obj;
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-R-POP-SINGLE】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 修剪列表
     *
     * @param key 键 开始角标 结束角标
     * @return 弹出结构体
     */
    public void lTrim(String key, long start, long end) {
        try {
            log.debug("【REDIS】：EXEC L-TRIM; KEY:{}", key);
            this.redisTemplate.opsForList().trim(key, start, end);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-L-TRIM】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 释放锁
     *
     * @param key
     * @param value
     * @return
     */
    public Long releaseLock(String key, String value) {
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long releaseStatus = (Long) this.redisTemplate.execute(redisScript, Collections.singletonList(key), value);
        return releaseStatus;
    }

    /**
     * 排序集合添加
     *
     * @param key
     * @param value
     * @param score
     */
    public void zadd(String key, Object value, double score) {
        try {
            log.debug("【REDIS】：EXEC ZADD; KEY:{}, SCORE: {}", key, score);
            this.redisTemplate.opsForZSet().add(key, String.valueOf(value), score);
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-ZADD】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }

    /**
     * 排序结合获取所有
     *
     * @param key
     * @return
     */
    public Set zGetAll(String key) {
        try {
            log.debug("【REDIS】：EXEC ZRANGEALL; KEY:{}", key);
            Set range = this.redisTemplate.opsForZSet().range(key, 0, -1);
            return range;
        } catch (Exception e) {
            log.error("【REDIS-CLIENT-ZRANGEALL】：{}", e.getMessage());
            throw new RedisException(ErrorCodeMsg.REDIS_ERROR);
        }
    }
}
