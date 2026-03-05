package com.sq.sjc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehouseSaveDto {
    private Long id;
    private String warehouseName;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String contactName;
    private String contactPhone;
    private BigDecimal capacity;
}
