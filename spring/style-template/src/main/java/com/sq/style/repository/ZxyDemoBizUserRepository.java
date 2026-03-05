package com.sq.style.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.style.entity.ZxyDemoBizUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("zxq")
public interface ZxyDemoBizUserRepository extends BaseMapper<ZxyDemoBizUserEntity> {

    @Select("SELECT * FROM user WHERE sys_id = #{sysId} AND (is_deleted = 0 OR is_deleted IS NULL) ORDER BY id DESC LIMIT 1")
    ZxyDemoBizUserEntity getUserBySysId(@Param("sysId") Long sysId);
}
