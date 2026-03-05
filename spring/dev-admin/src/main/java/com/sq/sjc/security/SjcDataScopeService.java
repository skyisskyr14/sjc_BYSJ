package com.sq.sjc.security;

import com.sq.system.common.exception.BizException;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.usercore.entity.UserEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class SjcDataScopeService {
    @Resource
    private SjcUserWarehouseRepository repository;

    public void checkWarehouseScope(Long warehouseId) {
        UserEntity user = UserTokenContextHolder.get();
        if (user == null || user.getId() == null || warehouseId == null) return;
        long cnt = repository.countByUserAndWarehouse(user.getId(), warehouseId);
        if (cnt <= 0) throw new BizException("无该仓库数据操作权限");
    }
}
