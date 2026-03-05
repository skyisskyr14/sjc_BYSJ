package com.sq.sjc.dispatchrepo;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.sjc.dispatchentity.SjcDispatchTrackEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("zxq")
public interface SjcDispatchTrackRepository extends BaseMapper<SjcDispatchTrackEntity> {}
