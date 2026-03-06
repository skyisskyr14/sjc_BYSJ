package com.sq.sjc.outbox;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sjc_event_outbox")
public class SjcEventOutboxEntity {
    private Long id;
    private String eventKey;
    private String topic;
    private String payload;
    private String status;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
    private LocalDateTime sentTime;
    private String errorMsg;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
