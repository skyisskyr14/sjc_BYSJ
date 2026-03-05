package com.sq.sjc.model;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.sjc.dto.InventoryChangeDto;
import com.sq.sjc.dto.InventoryFlowQueryDto;
import com.sq.sjc.dto.InventoryPageQueryDto;
import com.sq.sjc.entity.SjcAlertEntity;
import com.sq.sjc.entity.SjcInventoryEntity;
import com.sq.sjc.entity.SjcInventoryFlowEntity;
import com.sq.sjc.enums.SjcWsMessageType;
import com.sq.sjc.config.SjcTopicNames;
import com.sq.sjc.outbox.SjcEventOutboxModel;
import com.sq.system.common.utils.JsonUtil;
import com.sq.sjc.repository.SjcAlertRepository;
import com.sq.sjc.repository.SjcInventoryFlowRepository;
import com.sq.sjc.repository.SjcInventoryRepository;
import com.sq.sjc.repository.SjcMaterialRepository;
import com.sq.sjc.vo.SjcInventoryFlowVo;
import com.sq.sjc.history.SjcHistoryWriter;
import com.sq.sjc.security.SjcDataScopeService;
import com.sq.sjc.vo.SjcInventoryVo;
import com.sq.sjc.ws.SjcRealtimeWebSocket;
import com.sq.system.common.exception.BizException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@DS("zxq")
public class SjcInventoryModel {
    @Resource
    private SjcInventoryRepository inventoryRepository;
    @Resource
    private SjcInventoryFlowRepository flowRepository;
    @Resource
    private SjcMaterialRepository materialRepository;
    @Resource
    private SjcAlertRepository alertRepository;
    @Resource
    private SjcDashboardModel dashboardModel;
    @Resource
    private SjcEventOutboxModel outboxModel;
    @Resource
    private SjcHistoryWriter historyWriter;
    @Resource
    private SjcDataScopeService dataScopeService;

    public com.sq.system.common.result.PageResult<SjcInventoryVo> page(InventoryPageQueryDto dto) {
        List<SjcInventoryVo> rows = inventoryRepository.listAllWithJoin().stream().filter(v ->
                (dto.getWarehouseId() == null || dto.getWarehouseId().equals(v.getWarehouseId()))
                        && (dto.getMaterialId() == null || dto.getMaterialId().equals(v.getMaterialId()))
                        && (!StringUtils.hasText(dto.getKeyword()) || v.getMaterialName().contains(dto.getKeyword()) || v.getWarehouseName().contains(dto.getKeyword()))
        ).collect(Collectors.toList());
        int from = (int) ((dto.getPageNum() - 1) * dto.getPageSize());
        int to = Math.min(rows.size(), from + (int) dto.getPageSize());
        List<SjcInventoryVo> list = from >= rows.size() ? List.of() : rows.subList(from, to);
        return new com.sq.system.common.result.PageResult<>(list, rows.size(), dto.getPageNum(), dto.getPageSize());
    }

    public com.sq.system.common.result.PageResult<SjcInventoryFlowVo> flowPage(InventoryFlowQueryDto dto) {
        List<SjcInventoryFlowVo> rows = flowRepository.listAllWithJoin().stream().filter(v ->
                (dto.getWarehouseId() == null || dto.getWarehouseId().equals(v.getWarehouseId()))
                        && (dto.getMaterialId() == null || dto.getMaterialId().equals(v.getMaterialId()))
                        && (!StringUtils.hasText(dto.getFlowType()) || dto.getFlowType().equals(v.getFlowType()))
                        && (!StringUtils.hasText(dto.getStatus()) || dto.getStatus().equals(v.getStatus()))
                        && (!StringUtils.hasText(dto.getKeyword()) || v.getMaterialName().contains(dto.getKeyword()) || v.getWarehouseName().contains(dto.getKeyword()))
                        && inRange(v.getCreateTime(), dto.getStartTime(), dto.getEndTime())
        ).collect(Collectors.toList());
        int from = (int) ((dto.getPageNum() - 1) * dto.getPageSize());
        int to = Math.min(rows.size(), from + (int) dto.getPageSize());
        List<SjcInventoryFlowVo> list = from >= rows.size() ? List.of() : rows.subList(from, to);
        return new com.sq.system.common.result.PageResult<>(list, rows.size(), dto.getPageNum(), dto.getPageSize());
    }

    @Transactional
    public void inbound(InventoryChangeDto dto) {
        changeStock(dto, "INBOUND");
    }

    @Transactional
    public void outbound(InventoryChangeDto dto) {
        changeStock(dto, "OUTBOUND");
    }

    private void changeStock(InventoryChangeDto dto, String type) {
        dataScopeService.checkWarehouseScope(dto.getWarehouseId());
        SjcInventoryEntity inventory = inventoryRepository.getOne(dto.getWarehouseId(), dto.getMaterialId());
        if (inventory == null) {
            inventory = new SjcInventoryEntity();
            inventory.setWarehouseId(dto.getWarehouseId());
            inventory.setMaterialId(dto.getMaterialId());
            inventory.setQtyTotal(BigDecimal.ZERO);
            inventory.setQtyAvailable(BigDecimal.ZERO);
            inventory.setQtyLocked(BigDecimal.ZERO);
            inventory.setIsDelete(0);
            inventory.setCreateTime(LocalDateTime.now());
            inventory.setUpdateTime(LocalDateTime.now());
            inventoryRepository.insert(inventory);
        }

        BigDecimal before = inventory.getQtyAvailable();
        BigDecimal delta = dto.getQty();
        if (delta == null || delta.compareTo(BigDecimal.ZERO) <= 0) throw new BizException("数量必须大于0");

        BigDecimal after;
        if ("INBOUND".equals(type)) {
            after = before.add(delta);
            inventory.setQtyTotal(inventory.getQtyTotal().add(delta));
        } else {
            after = before.subtract(delta);
            if (after.compareTo(BigDecimal.ZERO) < 0) throw new BizException("库存不足，禁止出库");
            inventory.setQtyTotal(inventory.getQtyTotal().subtract(delta));
        }
        inventory.setQtyAvailable(after);
        inventory.setLastChangeTime(LocalDateTime.now());
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryRepository.updateById(inventory);

        SjcInventoryFlowEntity flow = new SjcInventoryFlowEntity();
        flow.setBizNo(type + "-" + UUID.randomUUID().toString().replace("-", ""));
        flow.setWarehouseId(dto.getWarehouseId());
        flow.setMaterialId(dto.getMaterialId());
        flow.setFlowType(type);
        flow.setStatus("DONE");
        flow.setQty(delta);
        flow.setBeforeQty(before);
        flow.setAfterQty(after);
        flow.setRemark(dto.getRemark());
        flow.setCreateTime(LocalDateTime.now());
        flowRepository.insert(flow);
        log.info("event=inventory_change type={} warehouseId={} materialId={} qty={} before={} after={}", type, dto.getWarehouseId(), dto.getMaterialId(), delta, before, after);
        historyWriter.writeFlow(flow);
        outboxModel.append(SjcTopicNames.INVENTORY_FLOW, JsonUtil.toJson(flow));

        checkAndCreateLowStockAlert(inventory);
        SjcRealtimeWebSocket.broadcast(SjcWsMessageType.INVENTORY_METRICS_UPDATED, dashboardModel.metrics());
    }

    private void checkAndCreateLowStockAlert(SjcInventoryEntity inventory) {
        var material = materialRepository.selectById(inventory.getMaterialId());
        if (material == null || material.getWarnThreshold() == null) return;
        if (inventory.getQtyAvailable().compareTo(material.getWarnThreshold()) <= 0) {
            SjcAlertEntity alert = new SjcAlertEntity();
            alert.setAlertType("LOW_STOCK");
            alert.setAlertLevel("HIGH");
            alert.setStatus("UNCONFIRMED");
            alert.setWarehouseId(inventory.getWarehouseId());
            alert.setMaterialId(inventory.getMaterialId());
            alert.setTriggerValue(inventory.getQtyAvailable());
            alert.setThresholdValue(material.getWarnThreshold());
            alert.setAlertMessage("库存低于阈值，请及时补货");
            alert.setCreateTime(LocalDateTime.now());
            alertRepository.insert(alert);
            log.info("event=alert_created alertType={} warehouseId={} materialId={} trigger={} threshold={}", alert.getAlertType(), alert.getWarehouseId(), alert.getMaterialId(), alert.getTriggerValue(), alert.getThresholdValue());
            outboxModel.append(SjcTopicNames.ALERT_CREATED, JsonUtil.toJson(alert));
            SjcRealtimeWebSocket.broadcast(SjcWsMessageType.ALERT_CREATED, alert);
        }
    }

    private boolean inRange(LocalDateTime time, String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.hasText(start) && time.isBefore(LocalDateTime.parse(start, formatter))) return false;
        if (StringUtils.hasText(end) && time.isAfter(LocalDateTime.parse(end, formatter))) return false;
        return true;
    }
}
