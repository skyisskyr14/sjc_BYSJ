package com.sq.sjc.outbox;

import org.springframework.stereotype.Component;

@Component
public class SjcOutboxNoopDispatcher implements SjcOutboxDispatcher {
    @Override
    public void dispatch(SjcEventOutboxEntity entity) {
        // commit1阶段仅完成outbox落表与扫描框架，后续commit接入Kafka真实发送
    }
}
