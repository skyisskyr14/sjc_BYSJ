package com.sq.sjc.controller;

import com.sq.system.common.result.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sjc/task")
public class SjcDispatchController {
    @GetMapping("/list")
    public ResponseResult<?> list() {
        return ResponseResult.success(List.of(Map.of("taskNo", "TASK-DEMO-001", "status", "PENDING", "demandPoint", "临时安置点A")));
    }
}
