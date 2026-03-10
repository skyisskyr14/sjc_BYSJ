package com.sq.sjc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SjcInventoryFlowVo {
    private Long id;
    private String bizNo;
    private Long warehouseId;
    private String warehouseName;
    private Long materialId;
    private String materialName;
    private String flowType;
    private String status;
    private BigDecimal qty;
    private BigDecimal beforeQty;
    private BigDecimal afterQty;
    private String remark;
    private LocalDateTime createTime;
}

