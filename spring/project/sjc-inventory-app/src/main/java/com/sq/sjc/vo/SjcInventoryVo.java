package com.sq.sjc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SjcInventoryVo {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long materialId;
    private String materialName;
    private BigDecimal qtyTotal;
    private BigDecimal qtyAvailable;
    private BigDecimal qtyLocked;
    private BigDecimal warnThreshold;
    private LocalDateTime lastChangeTime;
}
