package com.sq.system.authapp.controller.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sq.system.admincore.entity.PermissionEntity;
import com.sq.system.admincore.entity.RoleEntity;
import com.sq.system.admincore.repository.PermissionRepository;
import com.sq.system.admincore.repository.RolePermissionRepository;
import com.sq.system.admincore.repository.RoleRepository;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fd/admin/rbac")
@Tag(name = "SS--管理端角色权限")
public class ZxyAdminRbacController {

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private PermissionRepository permissionRepository;

    @Resource
    private RolePermissionRepository rolePermissionRepository;

    @GetMapping("/role/list")
    @AdminLog(module = "admin-rbac", action = "角色列表")
    @Operation(summary = "角色列表")
    public ResponseResult<?> roleList() {
        List<RoleEntity> roles = roleRepository.selectList(new QueryWrapper<RoleEntity>().orderByAsc("id"));
        List<Map<String, Object>> data = roles.stream().map(role -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", role.getId());
            item.put("roleCode", role.getRoleCode());
            item.put("roleName", role.getRoleName());
            item.put("description", role.getDescription());
            item.put("permissionIds", rolePermissionRepository.selectPermIdsByRoleId(role.getId()));
            return item;
        }).collect(Collectors.toList());
        return ResponseResult.success(data);
    }

    @PostMapping("/role/create")
    @AdminLog(module = "admin-rbac", action = "创建角色")
    @Operation(summary = "创建角色")
    public ResponseResult<?> roleCreate(@RequestBody RoleEntity dto) {
        if (!StringUtils.hasText(dto.getRoleCode())) return ResponseResult.fail("角色编码不能为空");
        if (!StringUtils.hasText(dto.getRoleName())) return ResponseResult.fail("角色名称不能为空");

        QueryWrapper<RoleEntity> codeWrapper = new QueryWrapper<RoleEntity>().eq("role_code", dto.getRoleCode().trim());
        if (roleRepository.selectCount(codeWrapper) > 0) return ResponseResult.fail("角色编码已存在");

        RoleEntity entity = new RoleEntity();
        entity.setRoleCode(dto.getRoleCode().trim());
        entity.setRoleName(dto.getRoleName().trim());
        entity.setDescription(dto.getDescription());
        return roleRepository.insert(entity) > 0 ? ResponseResult.success("创建成功") : ResponseResult.fail("创建失败");
    }

    @PostMapping("/role/update")
    @AdminLog(module = "admin-rbac", action = "更新角色")
    @Operation(summary = "更新角色")
    public ResponseResult<?> roleUpdate(@RequestBody RoleEntity dto) {
        if (dto.getId() == null || dto.getId() <= 0) return ResponseResult.fail("角色ID错误");
        if (!StringUtils.hasText(dto.getRoleCode())) return ResponseResult.fail("角色编码不能为空");
        if (!StringUtils.hasText(dto.getRoleName())) return ResponseResult.fail("角色名称不能为空");

        RoleEntity entity = roleRepository.selectById(dto.getId());
        if (entity == null) return ResponseResult.fail("角色不存在");

        QueryWrapper<RoleEntity> codeWrapper = new QueryWrapper<RoleEntity>()
                .eq("role_code", dto.getRoleCode().trim())
                .ne("id", dto.getId());
        if (roleRepository.selectCount(codeWrapper) > 0) return ResponseResult.fail("角色编码已存在");

        entity.setRoleCode(dto.getRoleCode().trim());
        entity.setRoleName(dto.getRoleName().trim());
        entity.setDescription(dto.getDescription());
        return roleRepository.updateById(entity) > 0 ? ResponseResult.success("更新成功") : ResponseResult.fail("更新失败");
    }

    @PostMapping("/role/delete")
    @Transactional
    @AdminLog(module = "admin-rbac", action = "删除角色")
    @Operation(summary = "删除角色")
    public ResponseResult<?> roleDelete(@RequestParam Long id) {
        RoleEntity entity = roleRepository.selectById(id);
        if (entity == null) return ResponseResult.fail("角色不存在");
        rolePermissionRepository.deleteByRoleId(id);
        return roleRepository.deleteById(id) > 0 ? ResponseResult.success("删除成功") : ResponseResult.fail("删除失败");
    }

    @GetMapping("/role/permissions")
    @AdminLog(module = "admin-rbac", action = "查看角色权限")
    @Operation(summary = "查看角色权限")
    public ResponseResult<?> rolePermissions(@RequestParam Long roleId) {
        if (roleRepository.selectById(roleId) == null) return ResponseResult.fail("角色不存在");
        return ResponseResult.success(rolePermissionRepository.selectPermIdsByRoleId(roleId));
    }

    @PostMapping("/role/permissions/set")
    @Transactional
    @AdminLog(module = "admin-rbac", action = "设置角色权限")
    @Operation(summary = "设置角色权限")
    public ResponseResult<?> setRolePermissions(@RequestBody Map<String, Object> body) {
        Object roleIdObj = body.get("roleId");
        if (!(roleIdObj instanceof Number roleIdNum)) return ResponseResult.fail("角色ID错误");
        Long roleId = roleIdNum.longValue();
        if (roleRepository.selectById(roleId) == null) return ResponseResult.fail("角色不存在");

        List<Long> permissionIds = Collections.emptyList();
        Object permissionIdsObj = body.get("permissionIds");
        if (permissionIdsObj instanceof List<?> ids) {
            permissionIds = ids.stream()
                    .filter(Number.class::isInstance)
                    .map(Number.class::cast)
                    .map(Number::longValue)
                    .distinct()
                    .collect(Collectors.toList());
        }

        if (!permissionIds.isEmpty()) {
            Long count = permissionRepository.selectCount(new QueryWrapper<PermissionEntity>().in("id", permissionIds));
            if (count == null || count.intValue() != permissionIds.size()) return ResponseResult.fail("包含不存在的权限ID");
        }

        rolePermissionRepository.deleteByRoleId(roleId);
        if (!permissionIds.isEmpty()) rolePermissionRepository.insertBatch(roleId, permissionIds);
        return ResponseResult.success("设置成功");
    }

    @GetMapping("/permission/list")
    @AdminLog(module = "admin-rbac", action = "权限列表")
    @Operation(summary = "权限列表")
    public ResponseResult<?> permissionList() {
        QueryWrapper<PermissionEntity> wrapper = new QueryWrapper<PermissionEntity>()
                .orderByAsc("sort")
                .orderByAsc("id");
        return ResponseResult.success(permissionRepository.selectList(wrapper));
    }

    @PostMapping("/permission/create")
    @AdminLog(module = "admin-rbac", action = "创建权限")
    @Operation(summary = "创建权限")
    public ResponseResult<?> permissionCreate(@RequestBody PermissionEntity dto) {
        if (!StringUtils.hasText(dto.getPermCode())) return ResponseResult.fail("权限编码不能为空");
        if (!StringUtils.hasText(dto.getPermName())) return ResponseResult.fail("权限名称不能为空");

        QueryWrapper<PermissionEntity> codeWrapper = new QueryWrapper<PermissionEntity>().eq("perm_code", dto.getPermCode().trim());
        if (permissionRepository.selectCount(codeWrapper) > 0) return ResponseResult.fail("权限编码已存在");

        PermissionEntity entity = new PermissionEntity();
        entity.setPermCode(dto.getPermCode().trim());
        entity.setPermName(dto.getPermName().trim());
        entity.setType(dto.getType());
        entity.setModule(dto.getModule());
        entity.setSort(dto.getSort());
        entity.setDescription(dto.getDescription());
        return permissionRepository.insert(entity) > 0 ? ResponseResult.success("创建成功") : ResponseResult.fail("创建失败");
    }

    @PostMapping("/permission/update")
    @AdminLog(module = "admin-rbac", action = "更新权限")
    @Operation(summary = "更新权限")
    public ResponseResult<?> permissionUpdate(@RequestBody PermissionEntity dto) {
        if (dto.getId() == null || dto.getId() <= 0) return ResponseResult.fail("权限ID错误");
        if (!StringUtils.hasText(dto.getPermCode())) return ResponseResult.fail("权限编码不能为空");
        if (!StringUtils.hasText(dto.getPermName())) return ResponseResult.fail("权限名称不能为空");

        PermissionEntity entity = permissionRepository.selectById(dto.getId());
        if (entity == null) return ResponseResult.fail("权限不存在");

        QueryWrapper<PermissionEntity> codeWrapper = new QueryWrapper<PermissionEntity>()
                .eq("perm_code", dto.getPermCode().trim())
                .ne("id", dto.getId());
        if (permissionRepository.selectCount(codeWrapper) > 0) return ResponseResult.fail("权限编码已存在");

        entity.setPermCode(dto.getPermCode().trim());
        entity.setPermName(dto.getPermName().trim());
        entity.setType(dto.getType());
        entity.setModule(dto.getModule());
        entity.setSort(dto.getSort());
        entity.setDescription(dto.getDescription());
        return permissionRepository.updateById(entity) > 0 ? ResponseResult.success("更新成功") : ResponseResult.fail("更新失败");
    }

    @PostMapping("/permission/delete")
    @Transactional
    @AdminLog(module = "admin-rbac", action = "删除权限")
    @Operation(summary = "删除权限")
    public ResponseResult<?> permissionDelete(@RequestParam Long id) {
        PermissionEntity entity = permissionRepository.selectById(id);
        if (entity == null) return ResponseResult.fail("权限不存在");

        List<RoleEntity> roles = roleRepository.selectList(new QueryWrapper<RoleEntity>().select("id"));
        for (RoleEntity role : roles) {
            List<Long> permIds = rolePermissionRepository.selectPermIdsByRoleId(role.getId());
            if (permIds.contains(id)) {
                List<Long> nextPermIds = permIds.stream().filter(p -> !p.equals(id)).collect(Collectors.toList());
                rolePermissionRepository.deleteByRoleId(role.getId());
                if (!nextPermIds.isEmpty()) rolePermissionRepository.insertBatch(role.getId(), nextPermIds);
            }
        }

        permissionRepository.deleteById(id);
        return ResponseResult.success("删除成功");
    }
}
