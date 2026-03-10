package com.sq.sjc.task;

import com.sq.sjc.model.SjcDashboardModel;
import com.sq.sjc.ws.SjcRealtimeWebSocket;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SjcRealtimePullTask {
    @Value("${sjc.stream.mode:REAL}")
    private String streamMode;
    @Resource
    private SjcDashboardModel dashboardModel;
    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Scheduled(fixedDelayString = "${sjc.realtime.pull-interval-ms:5000}")
    public void run() {
        if (!"REAL".equalsIgnoreCase(streamMode)) return;
        Object metrics = dashboardModel.metrics();
        if (redisTemplate != null) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries("sjc:inventory:metrics:global");
            if (!map.isEmpty()) metrics = map;
        }
        SjcRealtimeWebSocket.broadcast("INVENTORY_METRICS_UPDATED", metrics);
        log.info("event=realtime_metrics_pushed mode={} source={}", streamMode, redisTemplate != null ? "redis_or_db" : "db");
    }
}
