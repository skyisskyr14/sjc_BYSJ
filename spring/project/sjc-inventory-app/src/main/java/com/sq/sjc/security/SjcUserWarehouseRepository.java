package com.sq.sjc.security;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("zxq")
public interface SjcUserWarehouseRepository extends BaseMapper<SjcUserWarehouseEntity> {
    @Select("SELECT COUNT(1) FROM sjc_user_warehouse WHERE sys_user_id=#{sysUserId} AND warehouse_id=#{warehouseId} AND is_delete=0")
    long countByUserAndWarehouse(@Param("sysUserId") Long sysUserId, @Param("warehouseId") Long warehouseId);
}
