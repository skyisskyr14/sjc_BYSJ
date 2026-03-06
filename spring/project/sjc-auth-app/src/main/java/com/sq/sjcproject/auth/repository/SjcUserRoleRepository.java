package com.sq.sjcproject.auth.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.sjcproject.auth.entity.SjcUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("zxq")
public interface SjcUserRoleRepository extends BaseMapper<SjcUserRoleEntity> {
}
