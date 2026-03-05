package com.sq.sjc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryChangeDto {
    private Long warehouseId;
    private Long materialId;
    private BigDecimal qty;
    private String remark;
}
