package com.sq.sjc.dto;

import lombok.Data;

@Data
public class SjcPageQueryDto {
    private long pageNum = 1;
    private long pageSize = 10;
    private String keyword;
}

