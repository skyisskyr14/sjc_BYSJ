package com.sq.sjc.security;

import com.sq.system.common.exception.BizException;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SjcRateLimitService {
    @Resource(required = false)
    private StringRedisTemplate redis;

    public void check(String key, int limit, int seconds) {
        if (redis == null) return;
        Long n = redis.opsForValue().increment(key);
        if (n != null && n == 1L) redis.expire(key, Duration.ofSeconds(seconds));
        if (n != null && n > limit) throw new BizException("请求过于频繁，请稍后重试");
    }
}
