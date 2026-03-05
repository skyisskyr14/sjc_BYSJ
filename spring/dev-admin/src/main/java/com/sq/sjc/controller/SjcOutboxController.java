package com.sq.sjc.controller;

import com.sq.sjc.dto.SjcPageQueryDto;
import com.sq.sjc.outbox.SjcOutboxModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/outbox")
public class SjcOutboxController {
    @Resource
    private SjcOutboxModel model;

    @GetMapping("/dead/page")
    public ResponseResult<?> deadPage(SjcPageQueryDto dto) {
        return ResponseResult.success(model.pageDead(dto));
    }

    @PostMapping("/retry")
    @AdminLog(action = "Outbox死信重试", module = "sjc")
    public ResponseResult<?> retry(@RequestParam Long id) {
        String err = model.retry(id);
        return err == null ? ResponseResult.success() : ResponseResult.fail(err);
    }
}
