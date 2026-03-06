package com.sq.sjc.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.sjc.entity.SjcInventoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface SjcInventoryRepository extends BaseMapper<SjcInventoryEntity> {

    @Select("""
            SELECT i.*, w.warehouse_name, m.material_name, m.warn_threshold
            FROM sjc_inventory i
            LEFT JOIN sjc_warehouse w ON i.warehouse_id = w.id
            LEFT JOIN sjc_material m ON i.material_id = m.id
            WHERE i.is_delete = 0
            ORDER BY i.id DESC
            """)
    List<com.sq.sjc.vo.SjcInventoryVo> listAllWithJoin();

    @Select("SELECT * FROM sjc_inventory WHERE warehouse_id=#{warehouseId} AND material_id=#{materialId} AND is_delete=0 LIMIT 1")
    SjcInventoryEntity getOne(@Param("warehouseId") Long warehouseId, @Param("materialId") Long materialId);
}
