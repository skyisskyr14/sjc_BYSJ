package com.sq.sjcproject.auth.controller;

import com.sq.sjcproject.auth.dto.SjcRegisterDto;
import com.sq.sjcproject.auth.model.SjcAuthRegisterModel;
import com.sq.system.common.result.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sjc/auth")
@Tag(name = "SJC认证")
@RequiredArgsConstructor
public class SjcAuthController {
    private final SjcAuthRegisterModel model;

    @PostMapping("/register")
    @Operation(summary = "SJC用户注册")
    public ResponseResult<String> register(@RequestBody SjcRegisterDto dto) {
        String status = model.register(dto);
        if ("注册成功".equals(status)) return ResponseResult.success(status);
        return ResponseResult.fail(status);
    }
}
