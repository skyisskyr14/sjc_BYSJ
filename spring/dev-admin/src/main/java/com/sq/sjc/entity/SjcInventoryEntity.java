package com.sq.sjc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sjc_inventory")
public class SjcInventoryEntity {
    private Long id;
    private Long warehouseId;
    private Long materialId;
    private BigDecimal qtyTotal;
    private BigDecimal qtyAvailable;
    private BigDecimal qtyLocked;
    private LocalDateTime lastChangeTime;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
