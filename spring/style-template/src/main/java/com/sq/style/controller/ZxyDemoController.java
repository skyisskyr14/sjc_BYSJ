package com.sq.style.controller;

import com.sq.style.dto.ZxyDemoSaveDto;
import com.sq.style.model.ZxyDemoModel;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.usercore.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fd/demo")
@Tag(name = "SS--示例模板接口")
public class ZxyDemoController {

    @Resource
    private ZxyDemoModel demoModel;

    @GetMapping("/list")
    @UserLog(action = "用户查看演示列表", module = "style-template")
    @Operation(summary = "列表")
    public ResponseResult<?> list() {
        Long userId = currentBizUserId();
        if (userId == null) return ResponseResult.fail("用户不存在");
        return ResponseResult.success(demoModel.list(userId));
    }

    @GetMapping("/detail")
    @UserLog(action = "用户查看演示详情", module = "style-template")
    @Operation(summary = "查询详情")
    public ResponseResult<?> detail(@RequestParam Long id) {
        Long userId = currentBizUserId();
        if (userId == null) return ResponseResult.fail("用户不存在");
        var data = demoModel.detail(userId, id);
        if (data == null) return ResponseResult.fail("数据不存在");
        return ResponseResult.success(data);
    }

    @PostMapping("/create")
    @UserLog(action = "用户新增演示数据", module = "style-template")
    @Operation(summary = "新增")
    public ResponseResult<?> create(@RequestBody ZxyDemoSaveDto dto) {
        Long userId = currentBizUserId();
        if (userId == null) return ResponseResult.fail("用户不存在");
        String err = demoModel.create(userId, dto);
        if (err != null) return ResponseResult.fail(err);
        return ResponseResult.success("创建成功");
    }

    @PostMapping("/update")
    @UserLog(action = "用户修改演示数据", module = "style-template")
    @Operation(summary = "修改")
    public ResponseResult<?> update(@RequestBody ZxyDemoSaveDto dto) {
        Long userId = currentBizUserId();
        if (userId == null) return ResponseResult.fail("用户不存在");
        String err = demoModel.update(userId, dto);
        if (err != null) return ResponseResult.fail(err);
        return ResponseResult.success("修改成功");
    }

    @PostMapping("/delete")
    @AdminLog(action = "管理端删除演示数据", module = "style-template")
    @Operation(summary = "删除")
    public ResponseResult<?> delete(@RequestParam Long id) {
        String err = demoModel.delete(id);
        if (err != null) return ResponseResult.fail(err);
        return ResponseResult.success("删除成功");
    }

    private Long currentBizUserId() {
        UserEntity user = UserTokenContextHolder.get();
        return demoModel.getBizUserIdBySysUser(user);
    }
}
