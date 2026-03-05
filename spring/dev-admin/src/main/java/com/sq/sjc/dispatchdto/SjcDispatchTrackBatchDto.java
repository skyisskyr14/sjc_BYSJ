package com.sq.sjc.dispatchdto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SjcDispatchTrackBatchDto {
    @NotEmpty
    @Valid
    private List<SjcDispatchTrackReportDto> points;
}
