package com.sq.sjc.history;

import com.sq.sjc.entity.SjcInventoryFlowEntity;

public interface SjcHistoryWriter {
    void writeFlow(SjcInventoryFlowEntity flow);
}
