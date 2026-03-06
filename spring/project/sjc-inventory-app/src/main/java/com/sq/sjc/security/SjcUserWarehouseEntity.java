package com.sq.sjc.security;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sjc_user_warehouse")
public class SjcUserWarehouseEntity {
    private Long id;
    private Long sysUserId;
    private Long warehouseId;
    private Integer isDelete;
}
