package com.sq.sjc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sjc_alert")
public class SjcAlertEntity {
    private Long id;
    private String alertType;
    private String alertLevel;
    private String status;
    private Long warehouseId;
    private Long materialId;
    private BigDecimal triggerValue;
    private BigDecimal thresholdValue;
    private String alertMessage;
    private LocalDateTime ackTime;
    private LocalDateTime createTime;
}
