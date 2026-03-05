package com.sq.style.model;

import com.sq.style.dto.ZxyDemoSaveDto;
import com.sq.style.entity.ZxyDemoBizUserEntity;
import com.sq.style.entity.ZxyDemoEntity;
import com.sq.style.repository.ZxyDemoBizUserRepository;
import com.sq.style.repository.ZxyDemoRepository;
import com.sq.style.vo.ZxyDemoVo;
import com.sq.system.usercore.entity.UserEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ZxyDemoModel {

    @Resource
    private ZxyDemoRepository demoRepository;
    @Resource
    private ZxyDemoBizUserRepository zxyDemoBizUserRepository;

    public Long getBizUserIdBySysUser(UserEntity user) {
        if (user == null || user.getId() == null) return null;
        ZxyDemoBizUserEntity zxyUser = zxyDemoBizUserRepository.getUserBySysId(user.getId());
        return zxyUser == null ? null : zxyUser.getId();
    }

    public List<ZxyDemoVo> list(Long userId) {
        return demoRepository.listAllByUserId(userId).stream().map(this::toVo).collect(Collectors.toList());
    }

    public ZxyDemoVo detail(Long userId, Long id) {
        ZxyDemoEntity entity = demoRepository.getByIdAndUserId(id, userId);
        return entity == null ? null : toVo(entity);
    }

    public String create(Long userId, ZxyDemoSaveDto dto) {
        String valid = validateCreate(dto);
        if (valid != null) return valid;

        ZxyDemoEntity entity = new ZxyDemoEntity();
        entity.setUserId(userId);
        entity.setName(dto.getName().trim());
        entity.setRemark(trimToNull(dto.getRemark()));
        entity.setIsDelete(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        return demoRepository.insert(entity) > 0 ? null : "创建失败";
    }

    public String update(Long userId, ZxyDemoSaveDto dto) {
        String valid = validateUpdate(dto);
        if (valid != null) return valid;

        ZxyDemoEntity entity = demoRepository.getByIdAndUserId(dto.getId(), userId);
        if (entity == null) return "数据不存在";

        entity.setName(dto.getName().trim());
        entity.setRemark(trimToNull(dto.getRemark()));
        entity.setUpdateTime(LocalDateTime.now());

        return demoRepository.updateById(entity) > 0 ? null : "更新失败";
    }

    public String delete(Long id) {
        if (id == null || id <= 0) return "ID错误";
        ZxyDemoEntity entity = demoRepository.selectById(id);
        if (entity == null || (entity.getIsDelete() != null && entity.getIsDelete() == 1)) return "数据不存在";

        entity.setIsDelete(1);
        entity.setUpdateTime(LocalDateTime.now());
        return demoRepository.updateById(entity) > 0 ? null : "删除失败";
    }

    private String validateCreate(ZxyDemoSaveDto dto) {
        if (dto == null) return "请求参数为空";
        if (!StringUtils.hasText(dto.getName())) return "名称不能为空";
        if (dto.getName().trim().length() > 50) return "名称长度不能超过50";
        if (StringUtils.hasText(dto.getRemark()) && dto.getRemark().trim().length() > 255) return "备注长度不能超过255";
        return null;
    }

    private String validateUpdate(ZxyDemoSaveDto dto) {
        if (dto == null) return "请求参数为空";
        if (dto.getId() == null || dto.getId() <= 0) return "ID错误";
        return validateCreate(dto);
    }

    private String trimToNull(String text) {
        if (!StringUtils.hasText(text)) return null;
        return text.trim();
    }

    private ZxyDemoVo toVo(ZxyDemoEntity entity) {
        ZxyDemoVo vo = new ZxyDemoVo();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
