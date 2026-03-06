package com.sq.sjc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sjc_material")
public class SjcMaterialEntity {
    private Long id;
    private String materialName;
    private String materialType;
    private String spec;
    private String unit;
    private Integer shelfLifeDays;
    private BigDecimal warnThreshold;
    private Integer isDelete;
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
