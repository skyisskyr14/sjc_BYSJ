package com.sq.sjc.dispatchdto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
public class SjcDispatchTrackReportDto {
    @NotNull
    private Long taskId;
    @NotNull
    private BigDecimal longitude;
    @NotNull
    private BigDecimal latitude;
    private String ts;
}
