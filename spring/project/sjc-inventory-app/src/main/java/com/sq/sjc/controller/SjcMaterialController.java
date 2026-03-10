package com.sq.sjc.controller;

import com.sq.sjc.dto.MaterialSaveDto;
import com.sq.sjc.dto.SjcPageQueryDto;
import com.sq.sjc.model.SjcMaterialModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.result.PageResult;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.annotation.SjcRoleLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/material")
@Tag(name = "SJC-Material")
public class SjcMaterialController {
    @Resource
    private SjcMaterialModel model;

    @GetMapping("/page")
    @UserLog(action = "物资分页查询", module = "sjc")
    @Operation(summary = "物资分页")
    public ResponseResult<?> page(SjcPageQueryDto dto) {
        return ResponseResult.success(PageResult.of(model.page(dto)));
    }

    @PostMapping("/create")
    @AdminLog(action = "新增物资", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> create(@RequestBody MaterialSaveDto dto) {
        String err = model.create(dto);
        if (err != null) return ResponseResult.fail(err);
        return ResponseResult.success();
    }

    @PostMapping("/update")
    @AdminLog(action = "修改物资", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> update(@RequestBody MaterialSaveDto dto) {
        String err = model.update(dto);
        if (err != null) return ResponseResult.fail(err);
        return ResponseResult.success();
    }

    @PostMapping("/delete")
    @AdminLog(action = "删除物资", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> delete(@RequestParam Long id) {
        String err = model.delete(id);
        if (err != null) return ResponseResult.fail(err);
        return ResponseResult.success();
    }
}
