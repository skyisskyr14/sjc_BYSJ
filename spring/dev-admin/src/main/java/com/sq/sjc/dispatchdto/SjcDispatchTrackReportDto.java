package com.sq.sjc.dispatchdto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SjcDispatchTrackReportDto {
    private Long taskId;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String ts;
}
