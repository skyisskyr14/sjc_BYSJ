package com.sq.sjc.dto;

import lombok.Data;

@Data
public class InventoryPageQueryDto extends SjcPageQueryDto {
    private Long warehouseId;
    private Long materialId;
}
