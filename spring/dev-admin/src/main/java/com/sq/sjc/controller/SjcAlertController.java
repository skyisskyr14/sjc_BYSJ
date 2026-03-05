package com.sq.sjc.controller;

import com.sq.sjc.dto.AlertAckDto;
import com.sq.sjc.dto.AlertQueryDto;
import com.sq.sjc.model.SjcAlertModel;
import com.sq.system.common.result.PageResult;
import com.sq.system.common.result.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/alert")
public class SjcAlertController {
    @Resource
    private SjcAlertModel model;

    @GetMapping("/page")
    public ResponseResult<?> page(AlertQueryDto dto) {
        return ResponseResult.success(PageResult.of(model.page(dto)));
    }

    @PostMapping("/ack")
    public ResponseResult<?> ack(@RequestBody AlertAckDto dto) {
        String err = model.ack(dto);
        return err == null ? ResponseResult.success() : ResponseResult.fail(err);
    }
}
