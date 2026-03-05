package com.sq.sjc.task;

import com.sq.sjc.dto.InventoryChangeDto;
import com.sq.sjc.model.SjcInventoryModel;
import com.sq.sjc.repository.SjcInventoryRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class SjcSimulatorTask {
    @Value("${sjc.simulator.enabled:false}")
    private boolean enabled;
    @Value("${sjc.stream.mode:REAL}")
    private String streamMode;
    @Resource
    private SjcInventoryRepository inventoryRepository;
    @Resource
    private SjcInventoryModel inventoryModel;
    private final Random random = new Random();

    @Scheduled(fixedDelayString = "${sjc.simulator.interval-ms:8000}")
    public void run() {
        if (!enabled || "REAL".equalsIgnoreCase(streamMode)) return;
        var list = inventoryRepository.selectList(null);
        if (list.isEmpty()) return;
        var pick = list.get(random.nextInt(list.size()));
        InventoryChangeDto dto = new InventoryChangeDto();
        dto.setWarehouseId(pick.getWarehouseId());
        dto.setMaterialId(pick.getMaterialId());
        dto.setQty(BigDecimal.ONE);
        dto.setRemark("模拟事件");
        try {
            if (random.nextBoolean()) inventoryModel.inbound(dto); else inventoryModel.outbound(dto);
        } catch (Exception e) {
            log.info("模拟出库触发校验: {}", e.getMessage());
        }
    }
}
