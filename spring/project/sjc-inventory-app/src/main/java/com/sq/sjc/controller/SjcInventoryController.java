package com.sq.sjc.controller;

import com.sq.sjc.dto.InventoryChangeDto;
import com.sq.sjc.dto.InventoryFlowQueryDto;
import com.sq.sjc.dto.InventoryPageQueryDto;
import com.sq.sjc.model.SjcInventoryModel;
import com.sq.sjc.security.SjcRateLimitService;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.annotation.SjcRoleLimit;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/inventory")
@Tag(name = "SJC-Inventory")
@Validated
public class SjcInventoryController {
    @Resource
    private SjcInventoryModel model;
    @Resource
    private SjcRateLimitService rateLimitService;

    @GetMapping("/page")
    public ResponseResult<?> page(InventoryPageQueryDto dto) {
        return ResponseResult.success(model.page(dto));
    }

    @GetMapping("/flow/page")
    public ResponseResult<?> flowPage(InventoryFlowQueryDto dto) {
        return ResponseResult.success(model.flowPage(dto));
    }

    @PostMapping("/inbound")
    @AdminLog(action = "库存入库", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> inbound(@RequestBody @Valid InventoryChangeDto dto) {
        rateLimitService.check("rl:inventory:inbound", 60, 60);
        model.inbound(dto);
        return ResponseResult.success();
    }

    @PostMapping("/outbound")
    @AdminLog(action = "库存出库", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> outbound(@RequestBody @Valid InventoryChangeDto dto) {
        rateLimitService.check("rl:inventory:outbound", 60, 60);
        model.outbound(dto);
        return ResponseResult.success();
    }
}
