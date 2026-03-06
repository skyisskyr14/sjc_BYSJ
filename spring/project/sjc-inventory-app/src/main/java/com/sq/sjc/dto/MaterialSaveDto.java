package com.sq.sjc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialSaveDto {
    private Long id;
    private String materialName;
    private String materialType;
    private String spec;
    private String unit;
    private Integer shelfLifeDays;
    private BigDecimal warnThreshold;
}
