package com.sq.sjc.model;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.sjc.dto.SjcPageQueryDto;
import com.sq.sjc.dto.WarehouseSaveDto;
import com.sq.sjc.entity.SjcWarehouseEntity;
import com.sq.sjc.repository.SjcWarehouseRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
public class SjcWarehouseModel {
    @Resource
    private SjcWarehouseRepository repository;

    public IPage<SjcWarehouseEntity> page(SjcPageQueryDto dto) {
        return repository.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()),
                new LambdaQueryWrapper<SjcWarehouseEntity>()
                        .eq(SjcWarehouseEntity::getIsDelete, 0)
                        .and(StringUtils.hasText(dto.getKeyword()), w -> w.like(SjcWarehouseEntity::getWarehouseName, dto.getKeyword())
                                .or().like(SjcWarehouseEntity::getAddress, dto.getKeyword()))
                        .orderByDesc(SjcWarehouseEntity::getId));
    }

    public String create(WarehouseSaveDto dto) {
        if (!StringUtils.hasText(dto.getWarehouseName())) return "仓库名称不能为空";
        SjcWarehouseEntity entity = new SjcWarehouseEntity();
        copy(dto, entity);
        entity.setIsDelete(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return repository.insert(entity) > 0 ? null : "新增失败";
    }

    public String update(WarehouseSaveDto dto) {
        if (dto.getId() == null) return "ID不能为空";
        SjcWarehouseEntity entity = repository.selectById(dto.getId());
        if (entity == null || entity.getIsDelete() == 1) return "数据不存在";
        copy(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        return repository.updateById(entity) > 0 ? null : "更新失败";
    }

    public String delete(Long id) {
        SjcWarehouseEntity entity = repository.selectById(id);
        if (entity == null || entity.getIsDelete() == 1) return "数据不存在";
        entity.setIsDelete(1);
        entity.setUpdateTime(LocalDateTime.now());
        return repository.updateById(entity) > 0 ? null : "删除失败";
    }

    private void copy(WarehouseSaveDto dto, SjcWarehouseEntity entity) {
        entity.setWarehouseName(dto.getWarehouseName());
        entity.setAddress(dto.getAddress());
        entity.setLongitude(dto.getLongitude());
        entity.setLatitude(dto.getLatitude());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setCapacity(dto.getCapacity());
    }
}
