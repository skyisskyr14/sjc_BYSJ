package com.sq.sjc.controller;

import com.sq.sjc.dispatchmodel.SjcDispatchModel;
import com.sq.system.common.result.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sjc/task")
public class SjcTaskCompatController {
    @Resource
    private SjcDispatchModel model;

    @GetMapping("/list")
    public ResponseResult<?> list() {
        return ResponseResult.success(model.list());
    }
}
