package com.sq.sjc.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SjcDashboardMetricsVo {
    private BigDecimal totalInventory;
    private Long lowStockAlerts;
    private Long todayOutboundCount;
}
