package com.sq.sjcproject.auth.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.sjcproject.auth.entity.SjcRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("zxq")
public interface SjcRoleRepository extends BaseMapper<SjcRoleEntity> {
    @Select("SELECT * FROM sjc_role WHERE role_code = #{roleCode} LIMIT 1")
    SjcRoleEntity findByRoleCode(String roleCode);
}
