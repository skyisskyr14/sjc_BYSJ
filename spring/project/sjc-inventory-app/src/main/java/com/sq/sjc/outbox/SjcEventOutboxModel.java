package com.sq.sjc.outbox;

import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@DS("zxq")
public class SjcEventOutboxModel {
    @Resource
    private SjcEventOutboxRepository repository;

    public void append(String topic, String payload) {
        SjcEventOutboxEntity e = new SjcEventOutboxEntity();
        e.setEventKey(UUID.randomUUID().toString().replace("-", ""));
        e.setTopic(topic);
        e.setPayload(payload);
        e.setStatus("PENDING");
        e.setRetryCount(0);
        e.setIsDelete(0);
        e.setCreateTime(LocalDateTime.now());
        e.setUpdateTime(LocalDateTime.now());
        repository.insert(e);
    }
}
