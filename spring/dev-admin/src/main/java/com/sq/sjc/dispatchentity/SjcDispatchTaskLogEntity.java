package com.sq.sjc.dispatchentity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sjc_dispatch_task_log")
public class SjcDispatchTaskLogEntity {
    private Long id;
    private Long taskId;
    private String fromStatus;
    private String toStatus;
    private Long opBy;
    private String remark;
    private LocalDateTime createTime;
}
