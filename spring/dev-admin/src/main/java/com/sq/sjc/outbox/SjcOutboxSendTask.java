package com.sq.sjc.outbox;

import com.baomidou.dynamic.datasource.annotation.DS;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@DS("zxq")
public class SjcOutboxSendTask {
    @Resource
    private SjcEventOutboxRepository repository;
    @Resource
    private SjcOutboxDispatcher dispatcher;

    @Scheduled(fixedDelayString = "${sjc.outbox.scan-interval-ms:5000}")
    public void run() {
        for (SjcEventOutboxEntity item : repository.scanPending()) {
            try {
                dispatcher.dispatch(item);
                item.setStatus("SENT");
                item.setSentTime(LocalDateTime.now());
                item.setErrorMsg(null);
            } catch (Exception e) {
                item.setRetryCount((item.getRetryCount() == null ? 0 : item.getRetryCount()) + 1);
                item.setErrorMsg(e.getMessage());
                item.setNextRetryTime(LocalDateTime.now().plusSeconds(Math.min(60, item.getRetryCount() * 5L)));
                log.warn("outbox发送失败 id={} retry={}", item.getId(), item.getRetryCount(), e);
            }
            item.setUpdateTime(LocalDateTime.now());
            repository.updateById(item);
        }
    }
}
