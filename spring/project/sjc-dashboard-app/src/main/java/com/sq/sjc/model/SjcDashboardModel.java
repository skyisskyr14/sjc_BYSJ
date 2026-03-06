package com.sq.sjc.model;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sq.sjc.entity.SjcAlertEntity;
import com.sq.sjc.entity.SjcInventoryEntity;
import com.sq.sjc.entity.SjcInventoryFlowEntity;
import com.sq.sjc.repository.SjcAlertRepository;
import com.sq.sjc.repository.SjcInventoryFlowRepository;
import com.sq.sjc.repository.SjcInventoryRepository;
import com.sq.sjc.vo.SjcDashboardMetricsVo;
import com.sq.sjc.vo.SjcTrendPointVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@DS("zxq")
public class SjcDashboardModel {
    @Resource
    private SjcInventoryRepository inventoryRepository;
    @Resource
    private SjcAlertRepository alertRepository;
    @Resource
    private SjcInventoryFlowRepository flowRepository;

    public SjcDashboardMetricsVo metrics() {
        SjcDashboardMetricsVo vo = new SjcDashboardMetricsVo();
        List<SjcInventoryEntity> all = inventoryRepository.selectList(new LambdaQueryWrapper<SjcInventoryEntity>().eq(SjcInventoryEntity::getIsDelete, 0));
        BigDecimal total = all.stream().map(SjcInventoryEntity::getQtyAvailable).reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalInventory(total);
        vo.setLowStockAlerts(alertRepository.selectCount(new LambdaQueryWrapper<SjcAlertEntity>()
                .eq(SjcAlertEntity::getStatus, "UNCONFIRMED").eq(SjcAlertEntity::getAlertType, "LOW_STOCK")));
        vo.setTodayOutboundCount(flowRepository.selectCount(new LambdaQueryWrapper<SjcInventoryFlowEntity>()
                .eq(SjcInventoryFlowEntity::getFlowType, "OUTBOUND")
                .ge(SjcInventoryFlowEntity::getCreateTime, LocalDateTime.now().toLocalDate().atStartOfDay())));
        return vo;
    }

    public List<SjcTrendPointVo> trend() {
        return flowRepository.trendLast7d();
    }
}
