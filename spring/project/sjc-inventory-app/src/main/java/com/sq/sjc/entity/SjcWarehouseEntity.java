package com.sq.sjc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sjc_warehouse")
public class SjcWarehouseEntity {
    private Long id;
    private String warehouseName;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String contactName;
    private String contactPhone;
    private BigDecimal capacity;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
