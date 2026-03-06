package com.sq.sjc.outbox;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sq.sjc.dto.SjcPageQueryDto;
import com.sq.system.common.result.PageResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
@DS("zxq")
public class SjcOutboxModel {
    @Resource
    private SjcEventOutboxRepository repository;

    public PageResult<SjcEventOutboxEntity> pageDead(SjcPageQueryDto dto) {
        var page = repository.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()),
                new LambdaQueryWrapper<SjcEventOutboxEntity>()
                        .eq(SjcEventOutboxEntity::getStatus, "DEAD")
                        .orderByDesc(SjcEventOutboxEntity::getId));
        return PageResult.of(page);
    }

    public String retry(Long id) {
        SjcEventOutboxEntity e = repository.selectById(id);
        if (e == null) return "记录不存在";
        e.setStatus("PENDING");
        e.setNextRetryTime(null);
        e.setErrorMsg(null);
        e.setUpdateTime(java.time.LocalDateTime.now());
        return repository.updateById(e) > 0 ? null : "重试提交失败";
    }
}
