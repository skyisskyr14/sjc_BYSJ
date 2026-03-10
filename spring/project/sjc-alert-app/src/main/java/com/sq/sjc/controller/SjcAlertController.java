package com.sq.sjc.controller;

import com.sq.sjc.dto.AlertAckDto;
import com.sq.sjc.dto.AlertQueryDto;
import com.sq.sjc.model.SjcAlertModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.PageResult;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.annotation.SjcRoleLimit;
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
    @AdminLog(action = "确认预警", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN", "DISPATCHER"})
    public ResponseResult<?> ack(@RequestBody AlertAckDto dto) {
        String err = model.ack(dto);
        return err == null ? ResponseResult.success() : ResponseResult.fail(err);
    }
}
