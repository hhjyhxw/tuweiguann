package com.icloud.basecommon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class LockComponent {

//    @Autowired
//    private StringRedisTemplate lockRedisTemplate;
    @Autowired
    private RedisTemplate<String,String> stringRedisTemplate;


    private static final String LOCK_PREFIX = "LOCK_PREFIX_";

    /**
     * 获取乐观锁
     * @param key
     * @param timeoutSec 锁过期时间
     * @return
     */
    public boolean tryLock(String key, Integer timeoutSec) {
        return stringRedisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, System.currentTimeMillis() + "", Duration.ofSeconds(timeoutSec));
    }

    public boolean tryLockMulti(Collection<String> keys, Integer timeoutSec) {
        Map<String, String> map = new HashMap<>();
        String now = System.currentTimeMillis() + "";
        for (String key : keys) {
            map.put(key, now);
        }
        boolean suc = stringRedisTemplate.opsForValue().multiSetIfAbsent(map);
        if (suc) {
            keys.forEach(item -> {
                stringRedisTemplate.expire(item, timeoutSec, TimeUnit.SECONDS);
            });
        }
        return suc;
    }

    public void release(String key) {
        stringRedisTemplate.delete(LOCK_PREFIX + key);
    }

    public boolean hashPut(String table, String key) {
        return stringRedisTemplate.opsForHash().putIfAbsent(table, key, key);
    }

    public boolean hashContains(String table, String key) {
        return stringRedisTemplate.opsForHash().hasKey(table, key);
    }

    public void hashDel(String table, String key) {
        stringRedisTemplate.opsForHash().delete(table, key);
    }
}
