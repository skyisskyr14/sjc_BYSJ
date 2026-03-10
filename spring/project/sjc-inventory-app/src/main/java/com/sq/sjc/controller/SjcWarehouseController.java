package com.sq.sjc.controller;

import com.sq.sjc.dto.SjcPageQueryDto;
import com.sq.sjc.dto.WarehouseSaveDto;
import com.sq.sjc.model.SjcWarehouseModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.PageResult;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.annotation.SjcRoleLimit;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sjc/warehouse")
@Tag(name = "SJC-Warehouse")
public class SjcWarehouseController {
    @Resource
    private SjcWarehouseModel model;

    @GetMapping("/page")
    public ResponseResult<?> page(SjcPageQueryDto dto) {
        return ResponseResult.success(PageResult.of(model.page(dto)));
    }

    @PostMapping("/create")
    @AdminLog(action = "新增仓库", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> create(@RequestBody WarehouseSaveDto dto) {
        String err = model.create(dto);
        return err == null ? ResponseResult.success() : ResponseResult.fail(err);
    }

    @PostMapping("/update")
    @AdminLog(action = "修改仓库", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> update(@RequestBody WarehouseSaveDto dto) {
        String err = model.update(dto);
        return err == null ? ResponseResult.success() : ResponseResult.fail(err);
    }

    @PostMapping("/delete")
    @AdminLog(action = "删除仓库", module = "sjc")
    @SjcRoleLimit({"SYS_ADMIN", "WAREHOUSE_ADMIN"})
    public ResponseResult<?> delete(@RequestParam Long id) {
        String err = model.delete(id);
        return err == null ? ResponseResult.success() : ResponseResult.fail(err);
    }
}
