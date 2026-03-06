package com.sq.sjcproject.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sjc_role")
public class SjcRoleEntity {
    private Long id;
    private String roleCode;
    private String roleName;
    private String roleDesc;
    private Integer status;
}
