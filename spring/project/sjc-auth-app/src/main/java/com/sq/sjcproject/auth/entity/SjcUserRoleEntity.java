package com.sq.sjcproject.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sjc_user_role")
public class SjcUserRoleEntity {
    private Long id;
    private Long sysUserId;
    private Long roleId;
    private String roleCode;
}
