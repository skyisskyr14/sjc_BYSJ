package com.sq.style.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ZxyDemoVo {
    private Long id;
    private String name;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
