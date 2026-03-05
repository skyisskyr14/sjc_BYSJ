package com.sq.sjc.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.sjc.entity.SjcAlertEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("zxq")
public interface SjcAlertRepository extends BaseMapper<SjcAlertEntity> {
}
