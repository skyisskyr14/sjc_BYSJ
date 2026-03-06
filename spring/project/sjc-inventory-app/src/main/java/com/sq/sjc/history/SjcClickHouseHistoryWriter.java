package com.sq.sjc.history;

import com.sq.sjc.entity.SjcInventoryFlowEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "sjc.history", name = "engine", havingValue = "CLICKHOUSE")
public class SjcClickHouseHistoryWriter implements SjcHistoryWriter {
    @Override
    public void writeFlow(SjcInventoryFlowEntity flow) {
        log.debug("clickhouse writer placeholder, flowId={}", flow.getId());
    }
}
