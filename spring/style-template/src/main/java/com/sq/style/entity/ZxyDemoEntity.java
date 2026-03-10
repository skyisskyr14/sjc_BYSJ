package com.sq.style.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("demo_table")
public class ZxyDemoEntity {
    private Long id;
    private Long userId;
    private String name;
    private String remark;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
