package com.sq.style.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sq.style.entity.ZxyDemoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("zxq")
public interface ZxyDemoRepository extends BaseMapper<ZxyDemoEntity> {

    @Select("SELECT * FROM demo_table WHERE user_id = #{userId} AND (is_delete = 0 OR is_delete IS NULL) ORDER BY id DESC")
    List<ZxyDemoEntity> listAllByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM demo_table WHERE id = #{id} AND user_id = #{userId} AND (is_delete = 0 OR is_delete IS NULL) LIMIT 1")
    ZxyDemoEntity getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
