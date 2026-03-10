package com.sq.sjcproject.auth.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.sjcproject.auth.dto.SjcRegisterDto;
import com.sq.sjcproject.auth.model.SjcAuthRegisterModel;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.security.context.SjcRoleContextHolder;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.security.model.SjcUserRoleInfo;
import com.sq.system.security.resolver.SjcRoleResolver;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.repository.UserToRoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/sjc/auth")
@Tag(name = "SJC认证")
@RequiredArgsConstructor
public class SjcAuthController {
    private final SjcAuthRegisterModel model;
    private final SjcRoleResolver sjcRoleResolver;
    private final UserToRoleRepository userToRoleRepository;

    @PostMapping("/register")
    @Operation(summary = "SJC用户注册")
    public ResponseResult<String> register(@RequestBody SjcRegisterDto dto) {
        String status = model.register(dto);
        if (status != null && status.contains("成功")) return ResponseResult.success(status);
        return ResponseResult.fail(status);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户与SJC身份")
    public ResponseResult<?> me() {
        UserEntity user = UserTokenContextHolder.get();
        if (user == null) {
            return ResponseResult.fail("未登录");
        }

        SjcUserRoleInfo sjcRole = SjcRoleContextHolder.get();
        if (sjcRole == null) {
            sjcRole = sjcRoleResolver.resolveByUserId(user.getId());
        }
        if (sjcRole == null || sjcRole.getRoleCode() == null || sjcRole.getRoleCode().isBlank()) {
            sjcRole = new SjcUserRoleInfo(0L, "VIEWER", "访客");
        }

        String roleCode = sjcRole.getRoleCode();
        String roleName = sjcRole.getRoleName();

        UserToRoleEntity systemRole = userToRoleRepository.selectOne(
                Wrappers.lambdaQuery(UserToRoleEntity.class)
                        .eq(UserToRoleEntity::getUserId, user.getId())
                        .last("limit 1")
        );

        Set<String> inventoryWriteRoles = Set.of("SYS_ADMIN", "WAREHOUSE_ADMIN");
        Set<String> dispatchWriteRoles = Set.of("SYS_ADMIN", "DISPATCHER");
        Set<String> alertAckRoles = Set.of("SYS_ADMIN", "WAREHOUSE_ADMIN", "DISPATCHER");

        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("canInventoryWrite", inventoryWriteRoles.contains(roleCode));
        capabilities.put("canDispatchWrite", dispatchWriteRoles.contains(roleCode));
        capabilities.put("canAlertAck", alertAckRoles.contains(roleCode));

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("systemRoleId", systemRole == null ? null : systemRole.getRoleId());
        data.put("sjcRoleCode", roleCode);
        data.put("sjcRoleName", roleName);
        data.put("capabilities", capabilities);
        return ResponseResult.success(data);
    }
}
