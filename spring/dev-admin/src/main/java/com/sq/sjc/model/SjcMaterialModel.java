package com.sq.sjc.model;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.sjc.dto.MaterialSaveDto;
import com.sq.sjc.dto.SjcPageQueryDto;
import com.sq.sjc.entity.SjcMaterialEntity;
import com.sq.sjc.repository.SjcMaterialRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@DS("zxq")
public class SjcMaterialModel {
    @Resource
    private SjcMaterialRepository repository;

    public IPage<SjcMaterialEntity> page(SjcPageQueryDto dto) {
        Page<SjcMaterialEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return repository.selectPage(page, new LambdaQueryWrapper<SjcMaterialEntity>()
                .eq(SjcMaterialEntity::getIsDelete, 0)
                .and(StringUtils.hasText(dto.getKeyword()), w -> w.like(SjcMaterialEntity::getMaterialName, dto.getKeyword())
                        .or().like(SjcMaterialEntity::getMaterialType, dto.getKeyword()))
                .orderByDesc(SjcMaterialEntity::getId));
    }

    public String create(MaterialSaveDto dto) {
        if (!StringUtils.hasText(dto.getMaterialName())) return "物资名称不能为空";
        SjcMaterialEntity entity = new SjcMaterialEntity();
        copy(dto, entity);
        entity.setIsDelete(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return repository.insert(entity) > 0 ? null : "新增失败";
    }

    public String update(MaterialSaveDto dto) {
        if (dto.getId() == null) return "ID不能为空";
        SjcMaterialEntity entity = repository.selectById(dto.getId());
        if (entity == null || entity.getIsDelete() == 1) return "数据不存在";
        copy(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        return repository.updateById(entity) > 0 ? null : "更新失败";
    }

    public String delete(Long id) {
        SjcMaterialEntity entity = repository.selectById(id);
        if (entity == null || entity.getIsDelete() == 1) return "数据不存在";
        entity.setIsDelete(1);
        entity.setUpdateTime(LocalDateTime.now());
        return repository.updateById(entity) > 0 ? null : "删除失败";
    }

    private void copy(MaterialSaveDto dto, SjcMaterialEntity entity) {
        entity.setMaterialName(dto.getMaterialName());
        entity.setMaterialType(dto.getMaterialType());
        entity.setSpec(dto.getSpec());
        entity.setUnit(dto.getUnit());
        entity.setShelfLifeDays(dto.getShelfLifeDays());
        entity.setWarnThreshold(dto.getWarnThreshold());
    }
}
