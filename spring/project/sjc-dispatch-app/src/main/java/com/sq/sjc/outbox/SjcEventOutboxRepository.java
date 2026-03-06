package com.sq.sjc.outbox;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface SjcEventOutboxRepository extends BaseMapper<SjcEventOutboxEntity> {

    @Select("""
            SELECT * FROM sjc_event_outbox
            WHERE is_delete=0 AND status='PENDING'
            AND (next_retry_time IS NULL OR next_retry_time <= NOW())
            ORDER BY id ASC LIMIT 100
            """)
    List<SjcEventOutboxEntity> scanPending();
}
