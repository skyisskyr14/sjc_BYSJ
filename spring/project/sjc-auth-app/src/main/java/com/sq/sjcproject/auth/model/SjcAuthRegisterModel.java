package com.sq.sjcproject.auth.model;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.sjcproject.auth.dto.SjcRegisterDto;
import com.sq.sjcproject.auth.entity.SjcRoleEntity;
import com.sq.sjcproject.auth.entity.SjcUserRoleEntity;
import com.sq.sjcproject.auth.repository.SjcRoleRepository;
import com.sq.sjcproject.auth.repository.SjcUserRoleRepository;
import com.sq.system.captcha.support.CaptchaDispatcher;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserToProjectEntity;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.usercore.repository.UserToProjectRepository;
import com.sq.system.usercore.repository.UserToRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SjcAuthRegisterModel {
    private final CaptchaDispatcher captchaDispatcher;
    private final UserRepository userRepository;
    private final UserToRoleRepository userToRoleRepository;
    private final UserToProjectRepository userToProjectRepository;
    private final SjcRoleRepository sjcRoleRepository;
    private final SjcUserRoleRepository sjcUserRoleRepository;

    public String register(SjcRegisterDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) return "用户名不能为空";
        if (dto.getPassword() == null || dto.getPassword().isBlank()) return "密码不能为空";
        if (dto.getSecurePassword() == null || dto.getSecurePassword().isBlank()) return "安全密码不能为空";
        if (dto.getCaptchaUuid() == null || dto.getCaptchaInput() == null) return "验证码不能为空";

        String captchaType = (dto.getCaptchaType() == null || dto.getCaptchaType().isBlank()) ? "image" : dto.getCaptchaType();
        boolean valid = captchaDispatcher.get(captchaType).verify(dto.getCaptchaUuid(), dto.getCaptchaInput());
        if (!valid) return "验证码错误";

        if (userRepository.findByUsername(dto.getUsername()) != null) return "用户名已存在";

        String sjcRoleCode = (dto.getSjcRoleCode() == null || dto.getSjcRoleCode().isBlank()) ? "VIEWER" : dto.getSjcRoleCode();
        SjcRoleEntity sjcRole = sjcRoleRepository.findByRoleCode(sjcRoleCode);
        if (sjcRole == null) return "SJC角色不存在: " + sjcRoleCode;

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setSecurePassword(dto.getSecurePassword());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userRepository.insert(user);

        Long systemRoleId = dto.getSystemRoleId() == null ? 1L : dto.getSystemRoleId();
        Long projectId = dto.getProjectId() == null ? 1L : dto.getProjectId();

        UserToRoleEntity userToRole = new UserToRoleEntity();
        userToRole.setUserId(user.getId());
        userToRole.setRoleId(systemRoleId);
        userToRoleRepository.insert(userToRole);

        UserToProjectEntity userToProject = new UserToProjectEntity();
        userToProject.setUserId(user.getId());
        userToProject.setProjectId(projectId);
        userToProjectRepository.insert(userToProject);

        SjcUserRoleEntity exist = sjcUserRoleRepository.selectOne(Wrappers.lambdaQuery(SjcUserRoleEntity.class)
                .eq(SjcUserRoleEntity::getSysUserId, user.getId())
                .eq(SjcUserRoleEntity::getRoleId, sjcRole.getId())
                .last("limit 1"));
        if (exist == null) {
            SjcUserRoleEntity sjcUserRole = new SjcUserRoleEntity();
            sjcUserRole.setSysUserId(user.getId());
            sjcUserRole.setRoleId(sjcRole.getId());
            sjcUserRole.setRoleCode(sjcRole.getRoleCode());
            sjcUserRoleRepository.insert(sjcUserRole);
        }

        return "注册成功";
    }
}
