package com.sq.style.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class ZxyDemoBizUserEntity {
    private Long id;
    private Long sysId;
    private Integer isDeleted;
}
