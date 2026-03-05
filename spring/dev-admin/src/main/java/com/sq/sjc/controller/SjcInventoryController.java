package com.sq.sjc.controller;

import com.sq.sjc.dto.InventoryChangeDto;
import com.sq.sjc.dto.InventoryFlowQueryDto;
import com.sq.sjc.dto.InventoryPageQueryDto;
import com.sq.sjc.model.SjcInventoryModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/inventory")
@Tag(name = "SJC-库存")
public class SjcInventoryController {
    @Resource
    private SjcInventoryModel model;

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
    public ResponseResult<?> inbound(@RequestBody InventoryChangeDto dto) {
        model.inbound(dto);
        return ResponseResult.success();
    }

    @PostMapping("/outbound")
    @AdminLog(action = "库存出库", module = "sjc")
    public ResponseResult<?> outbound(@RequestBody InventoryChangeDto dto) {
        model.outbound(dto);
        return ResponseResult.success();
    }
}
