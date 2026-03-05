package com.sq.sjc.outbox;

public interface SjcOutboxDispatcher {
    void dispatch(SjcEventOutboxEntity entity) throws Exception;
}
