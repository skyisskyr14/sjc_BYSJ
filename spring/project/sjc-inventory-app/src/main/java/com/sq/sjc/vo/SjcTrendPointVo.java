package com.sq.sjc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SjcTrendPointVo {
    private String day;
    private BigDecimal inboundQty;
    private BigDecimal outboundQty;
}
