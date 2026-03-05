package com.sq.sjc.controller;

import com.sq.sjc.dispatchdto.SjcDispatchStatusDto;
import com.sq.sjc.dispatchdto.SjcDispatchTrackReportDto;
import com.sq.sjc.dispatchmodel.SjcDispatchModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/dispatch")
public class SjcDispatchController {
    @Resource
    private SjcDispatchModel model;

    @GetMapping("/task/list")
    public ResponseResult<?> list() { return ResponseResult.success(model.list()); }

    @PostMapping("/task/status")
    @AdminLog(action = "调度任务状态变更", module = "sjc")
    public ResponseResult<?> updateStatus(@RequestBody SjcDispatchStatusDto dto) {
        model.updateStatus(dto); return ResponseResult.success();
    }

    @GetMapping("/route")
    public ResponseResult<?> route(@RequestParam double fromLat,@RequestParam double fromLng,@RequestParam double toLat,@RequestParam double toLng) {
        return ResponseResult.success(model.route(fromLat, fromLng, toLat, toLng));
    }

    @PostMapping("/track/report")
    @AdminLog(action = "调度轨迹上报", module = "sjc")
    public ResponseResult<?> report(@RequestBody SjcDispatchTrackReportDto dto) {
        model.reportTrack(dto); return ResponseResult.success();
    }
}
