package com.sq.sjc.dto;

import lombok.Data;

@Data
public class InventoryFlowQueryDto extends SjcPageQueryDto {
    private Long warehouseId;
    private Long materialId;
    private String flowType;
    private String status;
    private String startTime;
    private String endTime;
}
