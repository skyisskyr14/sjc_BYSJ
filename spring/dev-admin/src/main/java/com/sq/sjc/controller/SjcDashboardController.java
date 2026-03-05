package com.sq.sjc.controller;

import com.sq.sjc.model.SjcDashboardModel;
import com.sq.system.common.result.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sjc/dashboard")
public class SjcDashboardController {
    @Resource
    private SjcDashboardModel model;

    @GetMapping("/metrics")
    public ResponseResult<?> metrics() {
        return ResponseResult.success(model.metrics());
    }

    @GetMapping("/trend")
    public ResponseResult<?> trend() {
        return ResponseResult.success(model.trend());
    }
}
