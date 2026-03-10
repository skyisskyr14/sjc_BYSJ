package com.sq.sjc.controller;

import com.sq.sjc.dispatchdto.SjcDispatchStatusDto;
import com.sq.sjc.dispatchdto.SjcDispatchTrackBatchDto;
import com.sq.sjc.dispatchdto.SjcDispatchTrackReportDto;
import com.sq.sjc.dispatchmodel.SjcDispatchModel;
import com.sq.sjc.security.SjcRateLimitService;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.annotation.SjcRoleLimit;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/dispatch")
@Validated
public class SjcDispatchController {
    @Resource
    private SjcDispatchModel model;
    @Resource
    private SjcRateLimitService rateLimitService;

    @GetMapping("/task/list")
    public ResponseResult<?> list() {
        return ResponseResult.success(model.list());
    }

    @PostMapping("/task/status")
    @AdminLog(action = "调度任务状态变更", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "DISPATCHER"})
    public ResponseResult<?> updateStatus(@RequestBody SjcDispatchStatusDto dto) {
        model.updateStatus(dto);
        return ResponseResult.success();
    }

    @GetMapping("/route")
    public ResponseResult<?> route(@RequestParam double fromLat, @RequestParam double fromLng, @RequestParam double toLat, @RequestParam double toLng) {
        return ResponseResult.success(model.route(fromLat, fromLng, toLat, toLng));
    }

    @PostMapping("/track/report")
    @AdminLog(action = "调度轨迹上报", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "DISPATCHER"})
    public ResponseResult<?> report(@RequestBody @Valid SjcDispatchTrackReportDto dto) {
        rateLimitService.check("rl:dispatch:track", 300, 60);
        model.reportTrack(dto);
        return ResponseResult.success();
    }

    @PostMapping("/track/batch")
    @AdminLog(action = "调度轨迹批量上报", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "DISPATCHER"})
    public ResponseResult<?> reportBatch(@RequestBody @Valid SjcDispatchTrackBatchDto dto) {
        rateLimitService.check("rl:dispatch:track:batch", 60, 60);
        model.reportTrackBatch(dto.getPoints());
        return ResponseResult.success();
    }
}
