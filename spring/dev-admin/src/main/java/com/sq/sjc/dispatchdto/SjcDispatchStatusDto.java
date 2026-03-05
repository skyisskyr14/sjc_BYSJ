package com.sq.sjc.dispatchdto;

import lombok.Data;

@Data
public class SjcDispatchStatusDto {
    private Long taskId;
    private String toStatus;
    private String remark;
}
