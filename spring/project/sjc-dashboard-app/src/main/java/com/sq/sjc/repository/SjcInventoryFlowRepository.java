package com.sq.sjc.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.sjc.entity.SjcInventoryFlowEntity;
import com.sq.sjc.vo.SjcTrendPointVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface SjcInventoryFlowRepository extends BaseMapper<SjcInventoryFlowEntity> {

    @Select("""
            SELECT f.id,f.biz_no,f.warehouse_id,w.warehouse_name,f.material_id,m.material_name,f.flow_type,f.status,f.qty,f.before_qty,f.after_qty,f.remark,f.create_time
            FROM sjc_inventory_flow f
            LEFT JOIN sjc_warehouse w ON w.id=f.warehouse_id
            LEFT JOIN sjc_material m ON m.id=f.material_id
            WHERE f.is_delete=0
            ORDER BY f.id DESC
            """)
    List<com.sq.sjc.vo.SjcInventoryFlowVo> listAllWithJoin();

    @Select("""
            SELECT DATE_FORMAT(create_time,'%Y-%m-%d') day,
            SUM(CASE WHEN flow_type='INBOUND' THEN qty ELSE 0 END) inboundQty,
            SUM(CASE WHEN flow_type='OUTBOUND' THEN qty ELSE 0 END) outboundQty
            FROM sjc_inventory_flow
            WHERE is_delete=0 AND create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d')
            ORDER BY day
            """)
    List<SjcTrendPointVo> trendLast7d();
}
