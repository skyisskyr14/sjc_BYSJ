package com.sq.sjc.dispatchentity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sjc_dispatch_task")
public class SjcDispatchTaskEntity {
    private Long id;
    private String taskNo;
    private Long fromWarehouseId;
    private String demandPointName;
    private String demandAddress;
    private String status;
    private Integer progressPercent;
    private LocalDateTime createTime;
}
