package com.sq.sjc.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
public class InventoryChangeDto {
    @NotNull
    private Long warehouseId;
    @NotNull
    private Long materialId;
    @NotNull
    @DecimalMin("0.0001")
    private BigDecimal qty;
    private String remark;
}
