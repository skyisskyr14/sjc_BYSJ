package com.sq.sjcproject.auth.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sjc_user_role")
public class SjcUserRoleEntity {
    private Long id;
    private Long sysUserId;
    private Long roleId;
    private int isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
