package com.sq.sjc.outbox;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
public class SjcKafkaOutboxDispatcher implements SjcOutboxDispatcher {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void dispatch(SjcEventOutboxEntity entity) throws Exception {
        kafkaTemplate.send(entity.getTopic(), entity.getEventKey(), entity.getPayload()).get();
    }
}
