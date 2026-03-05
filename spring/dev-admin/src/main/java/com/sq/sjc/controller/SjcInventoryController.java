package com.sq.sjc.controller;

import com.sq.sjc.dto.InventoryChangeDto;
import com.sq.sjc.dto.InventoryFlowQueryDto;
import com.sq.sjc.dto.InventoryPageQueryDto;
import com.sq.sjc.model.SjcInventoryModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.sjc.security.SjcRateLimitService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import com.sq.system.common.result.ResponseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/inventory")
@Tag(name = "SJC-库存")
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
    public ResponseResult<?> inbound(@RequestBody @Valid InventoryChangeDto dto) {
        rateLimitService.check("rl:inventory:inbound", 60, 60);
        model.inbound(dto);
        return ResponseResult.success();
    }

    @PostMapping("/outbound")
    @AdminLog(action = "库存出库", module = "sjc")
    public ResponseResult<?> outbound(@RequestBody @Valid InventoryChangeDto dto) {
        rateLimitService.check("rl:inventory:outbound", 60, 60);
        model.outbound(dto);
        return ResponseResult.success();
    }
}
