package com.sq.sjc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sjc_inventory_flow")
public class SjcInventoryFlowEntity {
    private Long id;
    private String bizNo;
    private Long warehouseId;
    private Long materialId;
    private String flowType;
    private String status;
    private BigDecimal qty;
    private BigDecimal beforeQty;
    private BigDecimal afterQty;
    private String remark;
    private LocalDateTime createTime;
}
