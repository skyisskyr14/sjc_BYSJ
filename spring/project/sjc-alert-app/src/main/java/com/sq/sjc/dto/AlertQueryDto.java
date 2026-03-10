package com.sq.sjc.dto;

import lombok.Data;

@Data
public class AlertQueryDto extends SjcPageQueryDto {
    private String status;
    private String alertType;
}
