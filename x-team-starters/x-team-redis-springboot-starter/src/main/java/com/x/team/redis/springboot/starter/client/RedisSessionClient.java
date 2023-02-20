package com.x.team.redis.springboot.starter.client;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 描述：Redisson客户端
 *
 * @author MassAdobe
 * @date Created in 2023/1/20 16:23
 */
@Component
public class RedisSessionClient {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 锁住不设置超时时间(拿不到lock就不罢休，不然线程就一直block)
     *
     * @param lockKey 锁头
     * @return org.redisson.api.RLock 锁实例
     * @author MassAdobe
     * @date Created in 2023/1/19 15:35
     */
    public RLock lock(String lockKey) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * leaseTime为加锁时间，单位为秒
     *
     * @param lockKey   锁头
     * @param leaseTime 释放锁的时间(秒)
     * @return org.redisson.api.RLock 锁实例
     * @author MassAdobe
     * @date Created in 2023/1/19 15:35
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * timeout为加锁时间，时间单位由unit确定
     *
     * @param lockKey 锁头
     * @param unit    释放锁的时间单位
     * @param timeout 释放锁的时间
     * @return org.redisson.api.RLock 锁实例
     * @author MassAdobe
     * @date Created in 2023/1/19 15:35
     */
    public RLock lock(String lockKey, TimeUnit unit, long timeout) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey   锁头
     * @param unit      释放锁的时间单位
     * @param waitTime  尝试时间
     * @param leaseTime 释放锁的时间
     * @return boolean 是否尝试成功
     * @author MassAdobe
     * @date Created in 2023/1/19 15:35
     */
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = this.redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 通过lockKey解锁
     *
     * @param lockKey 锁头
     * @author MassAdobe
     * @date Created in 2023/1/19 15:35
     */
    public void unlock(String lockKey) {
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 直接通过锁解锁
     *
     * @param lock 琐实例
     * @author MassAdobe
     * @date Created in 2023/1/19 15:35
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }

}
