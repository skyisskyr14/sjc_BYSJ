package com.sq.sjc.model;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.sjc.dto.AlertAckDto;
import com.sq.sjc.dto.AlertQueryDto;
import com.sq.sjc.entity.SjcAlertEntity;
import com.sq.sjc.repository.SjcAlertRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
public class SjcAlertModel {
    @Resource
    private SjcAlertRepository repository;

    public IPage<SjcAlertEntity> page(AlertQueryDto dto) {
        return repository.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), new LambdaQueryWrapper<SjcAlertEntity>()
                .eq(StringUtils.hasText(dto.getStatus()), SjcAlertEntity::getStatus, dto.getStatus())
                .eq(StringUtils.hasText(dto.getAlertType()), SjcAlertEntity::getAlertType, dto.getAlertType())
                .orderByDesc(SjcAlertEntity::getId));
    }

    public String ack(AlertAckDto dto) {
        if (dto.getId() == null) return "预警ID不能为空";
        SjcAlertEntity entity = repository.selectById(dto.getId());
        if (entity == null) return "预警不存在";
        entity.setStatus("ACKED");
        entity.setAckTime(LocalDateTime.now());
        return repository.updateById(entity) > 0 ? null : "确认失败";
    }
}
